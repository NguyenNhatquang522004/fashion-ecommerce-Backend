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

import io.github.nguyennhatquang.fashion.Catalog.domain.IRepository.IBrandRepository;
import io.github.nguyennhatquang.fashion.Catalog.domain.entity.Brand;
import io.github.nguyennhatquang.fashion.Catalog.infrastructure.Repository.BrandMongoRepository;
import io.github.nguyennhatquang.fashion.common.Utils.ConvertUtils;
import io.github.nguyennhatquang.fashion.common.Utils.CursorUtils;
import io.github.nguyennhatquang.fashion.common.request.ExactPageRequest;
import io.github.nguyennhatquang.fashion.common.request.PanigationRequest;
import io.github.nguyennhatquang.fashion.common.response.ExactPageResponse;
import io.github.nguyennhatquang.fashion.common.response.PanigationResponse;
import io.github.nguyennhatquang.fashion.common.shared.IdOnly;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class BrandRepositoryImpl implements IBrandRepository {

    private final BrandMongoRepository brandMongoRepository;

    @Override
    public Brand save(Brand brand) {
        return brandMongoRepository.save(brand);
    }

    @Override
    public List<Brand> saveAll(List<Brand> brands) {
        return brandMongoRepository.saveAll(brands);
    }

    @Override
    public Brand update(Brand brand) {
        return brandMongoRepository.save(brand);
    }

    @Override
    public List<Brand> updateAll(List<Brand> brands) {
        return brandMongoRepository.saveAll(brands);
    }

    @Override
    public void delete(Brand brand) {
        brandMongoRepository.delete(brand);
    }

    @Override
    public void deleteById(String id) {
        brandMongoRepository.deleteById(id);
    }

    @Override
    public void deleteAll(List<Brand> brands) {
        brandMongoRepository.deleteAll(brands);
    }

    @Override
    public void softDeleteById(String id) {
        brandMongoRepository.softDeleteById(id);
    }

    @Override
    public void softDeleteBySlug(String slug) {
        brandMongoRepository.softDeleteBySlug(slug);
    }

    @Override
    public Optional<Brand> findById(String id) {
        return brandMongoRepository.findById(id);
    }

    @Override
    public Optional<Brand> findBySlug(String slug) {
        return brandMongoRepository.findBySlug(slug);
    }

    @Override
    public List<Brand> findAll() {
        return brandMongoRepository.findAll();
    }

    @Override
    public ExactPageResponse<Brand> getBrandsExactPage(ExactPageRequest request) {
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
        long totalElements = brandMongoRepository.countBySnapshot(currentSnapshot);
        int totalPages = (int) Math.ceil((double) totalElements / limit);

        List<Brand> responseData = Collections.emptyList();

        if (totalElements > 0) {
            // Bước A: Deferred Join - Chỉ quét Index lấy ID
            List<String> ids = brandMongoRepository.findIdsBySnapshot(currentSnapshot, pageable)
                    .stream()
                    .map(IdOnly::getId)
                    .toList();

            // Bước B: Bốc hàng theo đúng ID đã chốt
            if (!ids.isEmpty()) {
                responseData = brandMongoRepository.fetchFullDataByIds(ids);
            }
        }
        LocalDateTime convertlocaldatetime = ConvertUtils.toLocalDateTime(currentSnapshot);
        // 4. Trả về Response
        return ExactPageResponse.<Brand>builder()
                .currentPage(request.getPage())
                .totalPages(totalPages)
                .totalElements(totalElements)
                .snapshotTime(convertlocaldatetime)
                .data(responseData)
                .build();
    }

    @Override
    public PanigationResponse<Brand> getBrandsCursor(PanigationRequest request) {
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

        List<Brand> data;
        boolean hasMore = false;
        boolean isFetchingNext = request.getHasNext() != null && request.getHasNext();

        // Xử lý truy vấn
        if (isFetchingNext || request.getCursor() == null) {
            // Cuộn xuống (Next) hoặc Lần gọi đầu tiên (không có cursor)
            data = brandMongoRepository.findNextPageDesc(cursorTime, cursorId, pageRequest);
        } else {
            // Cuộn lên (Previous)
            data = brandMongoRepository.findPreviousPageDesc(cursorTime, cursorId, pageRequest);
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
            Brand lastItem = data.get(data.size() - 1);
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

        return PanigationResponse.<Brand>builder()
                .cursor(nextCursor)
                .limit(limit)
                .sort("DESC")
                .hasNext(responseHasNext)
                .hasPrevious(responseHasPrevious)
                .data(data)
                .build();
    }
}
