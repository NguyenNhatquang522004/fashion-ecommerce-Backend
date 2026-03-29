package io.github.nguyennhatquang.fashion.Catalog.domain.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "product_catalog_index", createIndex = false) // createIndex=false vì ta tạo index bằng file JSON
                                                                    // trước để kiểm soát analyzer
public class SearchModel {

    @Id
    private String id;

    // Best Practice cho Multi-fields: Gom raw và suggest vào bên trong name
    @MultiField(mainField = @Field(type = FieldType.Text, analyzer = "vietnamese_search"), otherFields = {
            @InnerField(suffix = "raw", type = FieldType.Keyword),
            @InnerField(suffix = "suggest", type = FieldType.Text, analyzer = "vietnamese_autocomplete", searchAnalyzer = "vietnamese_search")
    })
    private String name;

    @Field(type = FieldType.Keyword)
    private String slug;

    @Field(type = FieldType.Text, analyzer = "vietnamese_search")
    private String description;

    // Bổ sung index = false để khớp với JSON
    @Field(name = "thumbnail_url", type = FieldType.Keyword, index = false)
    private String thumbnailUrl;

    // Đổi thành Object vì Brand chỉ là 1 thực thể đơn, không phải mảng (không cần
    // Nested)
    @Field(type = FieldType.Object)
    private Brand brand;

    // Categories là mảng object, cần query độc lập -> Dùng Nested
    @Field(type = FieldType.Nested)
    private List<Category> categories;

    // Pricing là 1 object gom nhóm -> Dùng Object
    @Field(type = FieldType.Object)
    private Pricing pricing;

    // Attributes chung của Product -> Dùng Nested
    @Field(type = FieldType.Nested)
    private List<Attribute> attributes;

    // Variants là mảng object cần query độc lập (vd: lọc variant còn hàng) -> Dùng
    // Nested
    @Field(type = FieldType.Nested)
    private List<Variant> variants;

    @Field(type = FieldType.Keyword)
    private String status;

    // Mapping chính xác tên field snake_case
    @Field(name = "is_active", type = FieldType.Boolean)
    private Boolean isActive;

    @Field(name = "is_deleted", type = FieldType.Boolean)
    private Boolean isDeleted;

    @Field(name = "created_at", type = FieldType.Date, format = DateFormat.date_time)
    private Instant createdAt;

    @Field(name = "updated_at", type = FieldType.Date, format = DateFormat.date_time)
    private Instant updatedAt;

    // --- Nested / Object Classes ---

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Brand {
        @Field(type = FieldType.Keyword)
        private String id;

        @Field(type = FieldType.Keyword)
        private String name;

        @Field(type = FieldType.Keyword)
        private String slug;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Category {
        @Field(type = FieldType.Keyword)
        private String id;

        @Field(type = FieldType.Keyword)
        private String name;

        @Field(type = FieldType.Keyword)
        private String slug;

        @Field(type = FieldType.Keyword)
        private String path;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Pricing {
        @Field(name = "base_price", type = FieldType.Double)
        private BigDecimal basePrice;

        @Field(name = "min_price", type = FieldType.Double)
        private BigDecimal minPrice;

        @Field(name = "max_price", type = FieldType.Double)
        private BigDecimal maxPrice;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Attribute {
        @Field(type = FieldType.Keyword)
        private String name;

        @Field(type = FieldType.Keyword)
        private List<String> options;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Variant {
        @Field(name = "sku_code", type = FieldType.Keyword) // Khớp tên field
        private String skuCode;

        @Field(type = FieldType.Keyword)
        private String barcode;

        @Field(name = "price_override", type = FieldType.Double)
        private BigDecimal priceOverride;

        // BẮT BUỘC đổi thành Object để tránh lỗi Nested-Inside-Nested của ES
        @Field(type = FieldType.Object)
        private List<VariantAttribute> attributes;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VariantAttribute {
        @Field(type = FieldType.Keyword)
        private String key;

        @Field(type = FieldType.Keyword)
        private String value;
    }
}