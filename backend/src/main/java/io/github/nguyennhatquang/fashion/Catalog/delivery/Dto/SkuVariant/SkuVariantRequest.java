package io.github.nguyennhatquang.fashion.Catalog.delivery.Dto.SkuVariant;

import java.math.BigDecimal;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.experimental.UtilityClass;

@UtilityClass
public class SkuVariantRequest {
    // --- 1. DTO cho API Create ---
    public record SkuVariantCreateRequest(
            @NotBlank(message = "Product ID không được để trống") String productId,

            @NotBlank(message = "Mã SKU không được để trống") String skuCode,

            String barcode,

            @NotEmpty(message = "Thuộc tính định danh biến thể không được để trống") @Valid List<SkuAttributeRequest> attributes,

            @DecimalMin(value = "0.0", inclusive = true, message = "Giá ghi đè không được số âm") BigDecimal priceOverride,

            List<String> media,

            @NotNull(message = "Trạng thái is_active là bắt buộc") Boolean isActive) {
    }

    // --- 2. DTO cho API Update ---
    public record SkuVariantUpdateRequest(
            // CỐ TÌNH BỎ productId Ở ĐÂY (Không cho phép chuyển SKU sang Product khác)

            @NotBlank(message = "Mã SKU không được để trống") String skuCode,

            String barcode,

            @NotEmpty(message = "Thuộc tính định danh biến thể không được để trống") @Valid List<SkuAttributeRequest> attributes,

            @DecimalMin(value = "0.0", inclusive = true, message = "Giá ghi đè không được số âm") BigDecimal priceOverride,

            List<String> media,

            @NotNull(message = "Trạng thái is_active là bắt buộc") Boolean isActive) {
    }

    public record SkuAttributeRequest(
            @NotBlank(message = "Khóa thuộc tính không được để trống (vd: Color)") String key,

            @NotBlank(message = "Giá trị thuộc tính không được để trống (vd: Red)") String value) {
    }
}
