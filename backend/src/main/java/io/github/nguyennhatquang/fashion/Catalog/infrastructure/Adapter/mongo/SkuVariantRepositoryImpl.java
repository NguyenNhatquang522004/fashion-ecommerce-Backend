package io.github.nguyennhatquang.fashion.Catalog.infrastructure.Adapter.mongo;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import io.github.nguyennhatquang.fashion.Catalog.domain.IRepository.ISkuVariantRepository;
import io.github.nguyennhatquang.fashion.Catalog.domain.entity.Product;
import io.github.nguyennhatquang.fashion.Catalog.domain.entity.SkuVariant;
import io.github.nguyennhatquang.fashion.Catalog.infrastructure.Repository.IdOnly;
import io.github.nguyennhatquang.fashion.Catalog.infrastructure.Repository.SkuVariantMongoRepository;
import io.github.nguyennhatquang.fashion.common.Utils.ConvertUtils;
import io.github.nguyennhatquang.fashion.common.Utils.CursorUtils;
import io.github.nguyennhatquang.fashion.common.request.ExactPageRequest;
import io.github.nguyennhatquang.fashion.common.request.PanigationRequest;
import io.github.nguyennhatquang.fashion.common.response.ExactPageResponse;
import io.github.nguyennhatquang.fashion.common.response.PanigationResponse;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class SkuVariantRepositoryImpl implements ISkuVariantRepository {

    private final SkuVariantMongoRepository skuVariantMongoRepository;

    @Override
    public SkuVariant save(SkuVariant skuVariant) {
        return skuVariantMongoRepository.save(skuVariant);
    }

    @Override
    public List<SkuVariant> saveAll(List<SkuVariant> skuVariants) {
        return skuVariantMongoRepository.saveAll(skuVariants);
    }

    @Override
    public SkuVariant update(SkuVariant skuVariant) {
        return skuVariantMongoRepository.save(skuVariant);
    }

    @Override
    public List<SkuVariant> updateAll(List<SkuVariant> skuVariants) {
        return skuVariantMongoRepository.saveAll(skuVariants);
    }

    @Override
    public void delete(SkuVariant skuVariant) {
        skuVariantMongoRepository.delete(skuVariant);
    }

    @Override
    public void deleteById(String id) {
        skuVariantMongoRepository.deleteById(id);
    }

    @Override
    public void deleteAll(List<SkuVariant> skuVariants) {
        skuVariantMongoRepository.deleteAll(skuVariants);
    }

    @Override
    public void softDeleteById(String id) {
        skuVariantMongoRepository.findById(id).ifPresent(skuVariant -> {
            skuVariant.setIsDeleted(true);
            skuVariantMongoRepository.save(skuVariant);
        });
    }

    @Override
    public void softDeleteBySkuCode(String skuCode) {
        skuVariantMongoRepository.findBySkuCode(skuCode).ifPresent(skuVariant -> {
            skuVariant.setIsDeleted(true);
            skuVariantMongoRepository.save(skuVariant);
        });
    }

    @Override
    public void softDeleteByProductId(String productId) {
        skuVariantMongoRepository.findByProductId(productId).forEach(skuVariant -> {
            skuVariant.setIsDeleted(true);
            skuVariantMongoRepository.save(skuVariant);
        });
    }

    @Override
    public Optional<SkuVariant> findById(String id) {
        return skuVariantMongoRepository.findById(id);
    }

    @Override
    public Optional<SkuVariant> findBySkuCode(String skuCode) {
        return skuVariantMongoRepository.findBySkuCode(skuCode);
    }

    @Override
    public List<SkuVariant> findByProductIdAndIsActiveTrue(String productId) {
        return skuVariantMongoRepository.findByProductIdAndIsActiveTrue(productId);
    }

    @Override
    public List<SkuVariant> findByProductIdAndIsDeletedFalse(String productId) {
        return skuVariantMongoRepository.findByProductIdAndIsDeletedFalse(productId);
    }

    @Override
    public List<SkuVariant> findByBarcode(String barcode) {
        return skuVariantMongoRepository.findByBarcode(barcode);
    }

    @Override
    public List<SkuVariant> findByProductId(String productId) {
        return skuVariantMongoRepository.findByProductId(productId);
    }

    @Override
    public List<SkuVariant> findAll() {
        return skuVariantMongoRepository.findAll();
    }

    @Override
    public ExactPageResponse<SkuVariant> getSkuVariantsExactPage(ExactPageRequest request) {
        // 1. Chốt Snapshot Time (Đóng băng Timeline)
        Instant currentSnapshot = request.getSnapshotTime() != null
                ? ConvertUtils.toInstant(request.getSnapshotTime())
                : Instant.now();

        // 2. Cấu hình Pageable (Kèm Sort cực kỳ quan trọng để ăn vào Index)
        int page = Math.max(request.getPage() - 1, 0);
        int limit = request.getLimit() > 0 ? request.getLimit() : 10;

        Pageable pageable = PageRequest.of(page, limit, Sort.by(
                Sort.Order.desc("createdAt"),
                Sort.Order.desc("id")));

        // 3. Lấy tổng số Element để tính Total Pages
        long totalElements = skuVariantMongoRepository.countBySnapshot(currentSnapshot);
        int totalPages = (int) Math.ceil((double) totalElements / limit);

        List<SkuVariant> responseData = Collections.emptyList();

        if (totalElements > 0) {
            // Bước A: Deferred Join - Chỉ quét Index lấy ID
            List<String> ids = skuVariantMongoRepository.findIdsBySnapshot(currentSnapshot, pageable)
                    .stream()
                    .map(IdOnly::getId)
                    .toList();

            // Bước B: Bốc hàng theo đúng ID đã chốt
            if (!ids.isEmpty()) {
                responseData = skuVariantMongoRepository.fetchFullDataByIds(ids);
            }
        }
        LocalDateTime convertlocaldatetime = ConvertUtils.toLocalDateTime(currentSnapshot);
        // 4. Trả về Response
        return ExactPageResponse.<SkuVariant>builder()
                .currentPage(request.getPage())
                .totalPages(totalPages)
                .totalElements(totalElements)
                .snapshotTime(convertlocaldatetime)
                .data(responseData)
                .build();
    }

    @Override
    public PanigationResponse<SkuVariant> getSkuVariantsCursor(PanigationRequest request) {
        int limit = request.getLimit() != null ? request.getLimit() : 10;
        // Fetch dư 1 record để check hasNext
        PageRequest pageRequest = PageRequest.of(0, limit + 1);

        Instant cursorTime = null;
        String cursorId = null;

        // Decode Cursor nếu có
        if (request.getCursor() != null) {
            Object[] decoded = CursorUtils.decodeCursor(request.getCursor());
            if (decoded != null) {
                cursorTime = ConvertUtils.toInstant((LocalDateTime) decoded[0]);
                cursorId = ConvertUtils.toString((UUID) decoded[1]);
            }
        }

        List<SkuVariant> data;
        boolean hasMore = false;
        boolean isFetchingNext = request.getHasNext() != null && request.getHasNext();

        // Xử lý truy vấn
        if (isFetchingNext || request.getCursor() == null) {
            // Cuộn xuống (Next) hoặc Lần gọi đầu tiên (không có cursor)
            data = skuVariantMongoRepository.findNextPageDesc(cursorTime, cursorId, pageRequest);
        } else {
            // Cuộn lên (Previous)
            data = skuVariantMongoRepository.findPreviousPageDesc(cursorTime, cursorId, pageRequest);
            // Vì ta query ASC để lấy phần tử phía trên, nên phải đảo ngược mảng lại cho
            // đúng thứ tự DESC
            Collections.reverse(data);
        }

        // Kiểm tra xem có vượt quá limit (nghĩa là còn trang tiếp) không
        if (data.size() > limit) {
            hasMore = true;
            if (isFetchingNext || request.getCursor() == null) {
                data.remove(data.size() - 1); // Bỏ phần tử dư ở cuối
            } else {
                data.remove(0); // Nếu là previous, phần tử dư nằm ở đầu sau khi reverse
            }
        }

        // Tạo Cursor mới dựa trên phần tử cuối cùng của list hiện tại
        String nextCursor = null;
        if (!data.isEmpty()) {
            SkuVariant lastItem = data.get(data.size() - 1);
            LocalDateTime convertCreatedAt = ConvertUtils.toLocalDateTime(lastItem.getCreatedAt());
            UUID convertId = ConvertUtils.toUUID(lastItem.getId());
            nextCursor = CursorUtils.encodeCursor(convertCreatedAt, convertId);
        }

        // Tính toán các cờ (Flags)
        boolean responseHasNext;
        boolean responseHasPrevious;

        if (request.getCursor() == null) {
            responseHasPrevious = false;
            responseHasNext = hasMore;
        } else if (isFetchingNext) {
            responseHasPrevious = true; // Chắc chắn có previous vì đã có cursor
            responseHasNext = hasMore;
        } else {
            responseHasNext = true; // Chắc chắn có next vì đang đi lùi lên
            responseHasPrevious = hasMore;
        }

        return PanigationResponse.<SkuVariant>builder()
                .cursor(nextCursor)
                .limit(limit)
                .sort("DESC")
                .hasNext(responseHasNext)
                .hasPrevious(responseHasPrevious)
                .data(data)
                .build();
    }
}
