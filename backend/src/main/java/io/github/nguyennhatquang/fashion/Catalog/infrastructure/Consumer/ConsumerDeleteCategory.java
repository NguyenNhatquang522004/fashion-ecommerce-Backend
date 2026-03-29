package io.github.nguyennhatquang.fashion.Catalog.infrastructure.Consumer;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.listener.BatchListenerFailedException;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import io.github.nguyennhatquang.fashion.Catalog.domain.IRepository.IProductRepository;
import io.github.nguyennhatquang.fashion.Catalog.domain.IRepository.ISkuVariantRepository;
import io.github.nguyennhatquang.fashion.Catalog.domain.entity.Product;
import io.github.nguyennhatquang.fashion.common.Enum.EventProcessStatus;
import io.github.nguyennhatquang.fashion.common.Enum.EventTopic.TopicName;
import io.github.nguyennhatquang.fashion.common.Enum.RedisKeyPrefix;
import io.github.nguyennhatquang.fashion.common.Payload.Category.DeleteBrandPayload;
import io.github.nguyennhatquang.fashion.common.Payload.Category.DeleteCategoryPayload;
import io.github.nguyennhatquang.fashion.common.Utils.ParseUtils;
import io.github.nguyennhatquang.fashion.common.errors.DatabaseTransientException;
import io.github.nguyennhatquang.fashion.common.errors.UnprocessablePayloadException;
import io.github.nguyennhatquang.fashion.common.infrastructure.auth.UserContextHolder;
import io.github.nguyennhatquang.fashion.common.kafka.EventContext;
import io.github.nguyennhatquang.fashion.common.kafka.IntegrationEvent;
import io.github.nguyennhatquang.fashion.common.response.ProcessResult;
import io.github.nguyennhatquang.fashion.common.shared.IRedis;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ConsumerDeleteCategory {

    private final IProductRepository productRepository;
    private final ISkuVariantRepository skuVariantRepository;
    @Qualifier("virtualThreadExecutor")
    private final ExecutorService virtualThreadExecutor;
    // Các Dependencies bắt buộc cho Infrastructure
    private final IRedis redis;
    private final RedissonClient redisson; // Thư viện Distributed Lock
    private final DeadLetterPublishingRecoverer recoverer; // Hứng Lỗi A

    // =========================================================================
    // 1. LUỒNG CHÍNH: BATCH LISTENER (NGƯỜI GÁC CỔNG)
    // =========================================================================
    @KafkaListener(topics = TopicName.BRAND_DELETE, groupId = "brand-delete-group", containerFactory = "batchKafkaListenerContainerFactory", concurrency = "3")
    public void listenBatch(List<ConsumerRecord<String, String>> records, Acknowledgment acknowledgment) {

        // Fix rủi ro NullPointerException nếu Key bị null
        Map<String, List<ConsumerRecord<String, String>>> groupedRecords = records.stream()
                .collect(Collectors.groupingBy(record -> record.key() != null ? record.key() : "NO_KEY"));

        // 1. Dùng CompletableFuture với Virtual Threads để xử lý song song
        List<CompletableFuture<Optional<ProcessResult>>> futures = groupedRecords.values().stream()
                .map(brandRecords -> CompletableFuture.supplyAsync(() -> {

                    // 3. BÊN TRONG THREAD NÀY: Chạy TUẦN TỰ từng sự kiện của cùng 1 Brand
                    for (ConsumerRecord<String, String> record : brandRecords) {
                        ProcessResult result = processSingleMessage(record);
                        if (!result.isSuccess()) {
                            return Optional.of(result); // Nếu có cái lỗi, dừng ngay nhánh này và báo lỗi
                        }
                    }
                    // FIX 1: Chỉ định rõ kiểu Generic cho Optional.empty()
                    return Optional.<ProcessResult>empty();

                }, virtualThreadExecutor))
                .toList();

        // 2. BARRIER: Đợi tất cả các Threads hoàn thành
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        // 3. TÌM MESSAGE LỖI (LỖI B) CÓ OFFSET THẤP NHẤT
        // FIX 2,3,4: Bóc vỏ Optional ra trước khi xử lý
        Optional<ProcessResult> firstFail = futures.stream()
                .map(CompletableFuture::join) // Lấy kết quả từ Thread (trả về Optional<ProcessResult>)
                .filter(Optional::isPresent) // Lọc bỏ những nhánh thành công (Optional rỗng)
                .map(Optional::get) // Bóc vỏ Optional ra, lúc này ta có Stream<ProcessResult> ròng
                .min(Comparator.comparingLong(res -> res.getRecord().offset())); // Tìm cái có offset nhỏ nhất

        if (firstFail.isPresent()) {
            ProcessResult failRes = firstFail.get();
            log.warn("Batch có lỗi tại offset {}. Kích hoạt Kafka Seek để Retry chặn dưới.",
                    failRes.getRecord().offset());
            // Ném lỗi để Kafka Retry từ vị trí này.
            throw new BatchListenerFailedException(
                    "Lỗi tại offset: " + failRes.getRecord().offset(),
                    failRes.getException(),
                    failRes.getRecord());
        }

        // 4. Nếu 100% thành công, chốt sổ cả Batch
        acknowledgment.acknowledge();
    }

    // =========================================================================
    // 2. XỬ LÝ ĐƠN LẺ: IDEMPOTENCY & ROUTING CHO TỪNG RECORD
    // =========================================================================
    private ProcessResult processSingleMessage(ConsumerRecord<String, String> record) {
        String eventId = ParseUtils.getHeaderValue(record.headers(), "X-Event-ID").orElse(null);
        String statusKey = null;
        String lockKey = null;

        try {
            // Bước 1: Parse JSON (Bắt Lỗi A ngay tại đây)
            IntegrationEvent<DeleteCategoryPayload> event = ParseUtils.parseEventSafe(record.value(),
                    DeleteCategoryPayload.class);
            DeleteCategoryPayload payload = event.payload();

            // Xác định Key định danh cho Redis
            String uniqueId = (eventId != null) ? eventId : "DELETE_CATEGORY_" + payload.getCategoryid();
            statusKey = RedisKeyPrefix.EVENT_STATUS.append(uniqueId);
            lockKey = RedisKeyPrefix.EVENT_LOCK.append(uniqueId);

            // Bước 2: KIỂM TRA NHANH TRẠNG THÁI (Fast Path)
            String status = redis.getAsString(statusKey);
            if (EventProcessStatus.isFinalStatus(status)) {
                log.info("Sự kiện {} đã có trạng thái {}. Bỏ qua.", uniqueId, status);
                return ProcessResult.success(record);
            }

            // Bước 3: TRANH QUYỀN XỬ LÝ BẰNG REDISSON (Distributed Lock)
            RLock lock = redisson.getLock(lockKey);
            boolean acquired = false;
            try {
                // Đợi tối đa 3s để lấy Lock. Lock tự nhả sau 10s nếu hệ thống chết ngỏm
                acquired = lock.tryLock(3, TimeUnit.SECONDS);

                if (!acquired) {
                    throw new DatabaseTransientException(
                            "Thread khác đang xử lý sự kiện " + uniqueId + ". Yêu cầu Retry.");
                }

                // Double-Checked Locking (Kiểm tra lại sau khi cầm Lock)
                status = redis.getAsString(statusKey);
                if (EventProcessStatus.isFinalStatus(status)) {
                    return ProcessResult.success(record);
                }

                // Thiết lập Context cho Log truy vết
                org.slf4j.MDC.put("correlationId", event.ctx().correlationId());

                // Bước 4: GỌI USE CASE THỰC THI NGHIỆP VỤ
                handleBusinessLogic(event.ctx(), event);

                // Đánh dấu Thành công vào Redis
                redis.setWithExpiration(statusKey, "SUCCESS", 7, TimeUnit.DAYS);
                return ProcessResult.success(record);

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new DatabaseTransientException("Bị ngắt quãng khi đợi Lock", e);
            } finally {
                org.slf4j.MDC.clear();
                // Giải phóng Lock
                if (acquired && lock.isHeldByCurrentThread()) {
                    lock.unlock();
                }
            }

        } catch (UnprocessablePayloadException e) {
            // LỖI A (Parse xịt, thiếu Data) -> Đẩy DLQ, đánh dấu ERROR_A, trả về Success để
            // luồng Batch đi tiếp
            log.error("Lỗi Format Data tại offset {}. Đẩy thẳng vào DLQ.", record.offset(), e);
            if (statusKey != null)
                redis.setWithExpiration(statusKey, EventProcessStatus.INVALID_PAYLOAD.name(), 7, TimeUnit.DAYS);
            recoverer.accept(record, e);
            return ProcessResult.success(record);

        } catch (Exception e) {
            // LỖI B (DB lỗi, Timeout, Tranh chấp Lock) -> Trả về Fail để luồng Batch ném ra
            // Exception gọi Kafka Retry
            log.warn("Lỗi hệ thống tạm thời tại offset {}. Chuẩn bị Retry.", record.offset(), e);
            if (statusKey != null)
                redis.setWithExpiration(statusKey, "ERROR_B", 7, TimeUnit.DAYS);
            return ProcessResult.fail(record, e);
        }
    }

    // =========================================================================
    // 3. USE CASE: TẦNG BUSINESS LOGIC LÕI
    // =========================================================================
    private void handleBusinessLogic(EventContext ctx, IntegrationEvent<DeleteCategoryPayload> event) {
        // 1. Check Timeout của Context
        ctx.throwIfExpired();

        DeleteCategoryPayload payload = event.payload();
        String categoryId = payload.getCategoryid();
        UserContextHolder.setUserId(ctx.userId());
        try {
            log.info("[{}] Thực thi xóa Brand: {}", ctx.correlationId(), categoryId);
            List<String> productIds = productRepository.softDeleteAndReturnIds(categoryId);
            productIds.stream().forEach(id -> {
                skuVariantRepository.softDeleteByProductId(id);
            });
        } finally {
            UserContextHolder.clear();
        }
    }
}
