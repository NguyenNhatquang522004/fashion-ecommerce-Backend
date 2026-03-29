package io.github.nguyennhatquang.fashion.Catalog.delivery.Dto.SearchModel;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchModelResponse {
    // 1. Danh sách sản phẩm (Đã được làm "phẳng" và tối giản cho UI)
    private List<ProductItem> items;

    // 2. Thông tin phân trang chuẩn RESTful
    private Pagination metadata;

    // 3. Thống kê bộ lọc (Dùng để vẽ thanh Sidebar Filter bên trái)
    private Facets aggregations;

    // ==========================================
    // CÁC CLASS CON (NESTED CLASSES)
    // ==========================================

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductItem {
        private String id;
        private String name;
        private String slug;
        private String productUrl; // Tự động ghép nối tại Backend
        private String thumbnailUrl;
        private BigDecimal minPrice;
        private BigDecimal maxPrice;
        private String brandName;
        // Có thể thêm cờ nhãn dán: isNew, isSaleOff... tùy nghiệp vụ
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Pagination {
        private int page; // Trang hiện tại (Bắt đầu từ 1 cho FE dễ dùng)
        private int size; // Số lượng trên 1 trang
        private long totalElements; // Tổng số sản phẩm tìm được
        private int totalPages; // Tổng số trang
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Facets {
        private List<FacetTerm> brands;
        private List<FacetTerm> categories;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FacetTerm {
        private String name; // Tên hiển thị (VD: "Nike")
        private long count; // Số lượng sản phẩm có (VD: 15)
    }
}
