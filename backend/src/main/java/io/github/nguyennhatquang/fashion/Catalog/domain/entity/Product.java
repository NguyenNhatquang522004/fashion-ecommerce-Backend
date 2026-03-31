package io.github.nguyennhatquang.fashion.Catalog.domain.entity;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import io.github.nguyennhatquang.fashion.common.Enum.ProductStatusEnum;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "products")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@CompoundIndexes({
        // Index gốc bạn đã có (dùng khi client không truyền sortBy, mặc định là
        // createdAt)
        @CompoundIndex(name = "snapshot_idx_created", def = "{'createdAt': -1, '_id': -1}"),

        // Index bổ sung NẾU bạn cho phép sort theo basePrice
        @CompoundIndex(name = "snapshot_idx_price", def = "{'basePrice': 1, 'createdAt': -1, '_id': -1}")
})
public class Product {
    @Id
    private String id;

    @NotBlank(message = "Tên sản phẩm không được để trống")
    @Field("name")
    private String name;

    @NotBlank(message = "Slug không được để trống")
    @Indexed(unique = true)
    @Field("slug")
    private String slug;

    @Indexed
    @Field(targetType = FieldType.OBJECT_ID, value = "brand_id")
    private String brandId;

    @Field("brand_name")
    private String brandName;

    @NotEmpty(message = "Sản phẩm phải thuộc ít nhất 1 danh mục")
    @Indexed
    @Field(targetType = FieldType.OBJECT_ID, value = "category_ids")
    @Builder.Default
    private List<String> categoryIds = new ArrayList<>();

    @Field("description")
    private String description;

    @NotNull(message = "Giá gốc không được để trống")
    @Field(targetType = FieldType.DECIMAL128, value = "base_price")
    private BigDecimal basePrice;

    @Valid
    @Field("attributes")
    @Builder.Default
    private List<ProductAttribute> attributes = new ArrayList<>();

    @Valid
    @Field("media")
    @Builder.Default
    private List<ProductMedia> media = new ArrayList<>();

    @NotNull(message = "Trạng thái không được để trống")
    @Indexed
    @Field("status")
    @Builder.Default
    private ProductStatusEnum status = ProductStatusEnum.DRAFT;

    @Field("seo")
    private ProductSeo seo;

    @Field("is_deleted")
    @Builder.Default
    private Boolean isDeleted = false;

    @CreatedDate
    @Field("created_at")
    private Instant createdAt;

    @LastModifiedDate
    @Field("updated_at")
    private Instant updatedAt;

    @CreatedBy
    @Field("created_by")
    private String createdBy;

    @LastModifiedBy
    @Field("updated_by")
    private String updatedBy;
}