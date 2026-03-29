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

import io.github.nguyennhatquang.fashion.Catalog.domain.IRepository.IProductRepository;

import io.github.nguyennhatquang.fashion.Catalog.domain.entity.Product;
import io.github.nguyennhatquang.fashion.Catalog.infrastructure.Repository.IdOnly;
import io.github.nguyennhatquang.fashion.Catalog.infrastructure.Repository.ProductMongoRepository;
import io.github.nguyennhatquang.fashion.common.Enum.ProductStatusEnum;
import io.github.nguyennhatquang.fashion.common.Utils.ConvertUtils;
import io.github.nguyennhatquang.fashion.common.Utils.CursorUtils;
import io.github.nguyennhatquang.fashion.common.request.ExactPageRequest;
import io.github.nguyennhatquang.fashion.common.request.PanigationRequest;
import io.github.nguyennhatquang.fashion.common.response.ExactPageResponse;
import io.github.nguyennhatquang.fashion.common.response.PanigationResponse;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryImpl implements IProductRepository {

    private final ProductMongoRepository productMongoRepository;

    @Override
    public Product save(Product product) {
        return productMongoRepository.save(product);
    }

    @Override
    public List<Product> saveAll(List<Product> products) {
        return productMongoRepository.saveAll(products);
    }

    @Override
    public Product update(Product product) {
        return productMongoRepository.save(product);
    }

    @Override
    public List<Product> updateAll(List<Product> products) {
        return productMongoRepository.saveAll(products);
    }

    @Override
    public void delete(Product product) {
        productMongoRepository.delete(product);
    }

    @Override
    public void deleteById(String id) {
        productMongoRepository.deleteById(id);
    }

    @Override
    public void deleteAll(List<Product> products) {
        if (products == null || products.isEmpty()) {
            return;
        }

        // 1. Chỉ lấy ra danh sách các ID
        List<String> productIds = products.stream()
                .map(Product::getId)
                .toList();

        // 2. Giao việc cho MongoDB tự xử lý siêu tốc
        productMongoRepository.softDeleteAllByIds(productIds);
    }

    @Override
    public void softDeleteById(String id) {
        // 1 chuyến xe duy nhất, Mongo tự động set cờ isDeleted mà không cần kéo dữ liệu
        // lên!
        productMongoRepository.softDeleteById(id);
    }

    @Override
    public void softDeleteBySlug(String slug) {
        // 1 chuyến xe duy nhất, Mongo tự động set cờ isDeleted mà không cần kéo dữ liệu
        // lên! productMongoRepository.softDeleteBySlug(slug);
    }

    @Override
    public void softDeleteAll(List<Product> products) {
        if (products == null || products.isEmpty()) {
            return;
        }

        // 1. Chỉ lấy ra danh sách các ID
        List<String> productIds = products.stream()
                .map(Product::getId)
                .toList();

        // 2. Giao việc cho MongoDB tự xử lý siêu tốc
        productMongoRepository.softDeleteAllByIds(productIds);
    }

    @Override
    public Optional<Product> findById(String id) {
        return productMongoRepository.findById(id);
    }

    @Override
    public Optional<Product> findByStatus(ProductStatusEnum status) {
        return productMongoRepository.findByStatus(status);
    }

    @Override
    public Optional<Product> findBySlug(String slug) {
        return productMongoRepository.findBySlug(slug);
    }

    @Override
    public List<Product> findAll() {
        return productMongoRepository.findAll();
    }

    @Override
    public ExactPageResponse<Product> getProductsExactPage(ExactPageRequest request) {
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
        long totalElements = productMongoRepository.countBySnapshot(currentSnapshot);
        int totalPages = (int) Math.ceil((double) totalElements / limit);

        List<Product> responseData = Collections.emptyList();

        if (totalElements > 0) {
            // Bước A: Deferred Join - Chỉ quét Index lấy ID
            List<String> ids = productMongoRepository.findIdsBySnapshot(currentSnapshot, pageable)
                    .stream()
                    .map(IdOnly::getId)
                    .toList();

            // Bước B: Bốc hàng theo đúng ID đã chốt
            if (!ids.isEmpty()) {
                responseData = productMongoRepository.fetchFullDataByIds(ids);
            }
        }
        LocalDateTime convertlocaldatetime = ConvertUtils.toLocalDateTime(currentSnapshot);
        // 4. Trả về Response
        return ExactPageResponse.<Product>builder()
                .currentPage(request.getPage())
                .totalPages(totalPages)
                .totalElements(totalElements)
                .snapshotTime(convertlocaldatetime)
                .data(responseData)
                .build();
    }

    @Override
    public PanigationResponse<Product> getProductsCursor(PanigationRequest request) {
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

        List<Product> data;
        boolean hasMore = false;
        boolean isFetchingNext = request.getHasNext() != null && request.getHasNext();

        // Xử lý truy vấn
        if (isFetchingNext || request.getCursor() == null) {
            // Cuộn xuống (Next) hoặc Lần gọi đầu tiên (không có cursor)
            data = productMongoRepository.findNextPageDesc(cursorTime, cursorId, pageRequest);
        } else {
            // Cuộn lên (Previous)
            data = productMongoRepository.findPreviousPageDesc(cursorTime, cursorId, pageRequest);
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
            Product lastItem = data.get(data.size() - 1);
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

        return PanigationResponse.<Product>builder()
                .cursor(nextCursor)
                .limit(limit)
                .sort("DESC")
                .hasNext(responseHasNext)
                .hasPrevious(responseHasPrevious)
                .data(data)
                .build();
    }

    @Override
    public List<Product> findProductsWithExactlyOneSpecificBrand(String brandId) {
        return productMongoRepository.findProductsWithExactlyOneSpecificBrand(brandId);
    }

    @Override
    public List<Product> findProductsWithExactlyOneSpecificCategory(String categoryId) {
        return productMongoRepository.findProductsWithExactlyOneSpecificCategory(categoryId);
    }
}
