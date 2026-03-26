package io.github.nguyennhatquang.fashion.Catalog.domain.entity;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

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

@Document(collection = "sku_variants")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@CompoundIndex(name = "snapshot_idx", def = "{'created_at': -1, '_id': -1}")
public class SkuVariant {

    @Id
    private String id;

    @NotBlank(message = "Product ID không được để trống")
    @Indexed // Tạo index để query danh sách SKU theo Product cực nhanh
    @Field(targetType = FieldType.OBJECT_ID, value = "product_id")
    private String productId;

    @NotBlank(message = "Mã SKU không được để trống")
    @Indexed(unique = true)
    @Field("sku_code")
    private String skuCode;

    @Field("barcode")
    private String barcode;

    @NotEmpty(message = "Thuộc tính định danh biến thể (Attributes) không được để trống")
    @Valid
    @Field("attributes")
    @Builder.Default
    private List<SkuAttribute> attributes = new ArrayList<>();

    // LƯU Ý: Không dùng @NotNull ở đây. Nếu null, tầng Service sẽ tự lấy base_price
    // của Product
    @Field(targetType = FieldType.DECIMAL128, value = "price_override")
    private BigDecimal priceOverride;

    @Field("media")
    @Builder.Default
    private List<String> media = new ArrayList<>(); // Mảng chứa URL ảnh riêng của biến thể

    @NotNull(message = "Trạng thái is_active là bắt buộc")
    @Field("is_active")
    @Builder.Default
    private Boolean isActive = true;

    // --- Soft Delete (Đã kế thừa từ các bài trước) ---
    @Field("is_deleted")
    @Builder.Default
    private Boolean isDeleted = false;

    // --- Auditing ---
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