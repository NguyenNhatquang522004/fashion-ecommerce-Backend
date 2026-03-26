package io.github.nguyennhatquang.fashion.Catalog.delivery.Dto.Product;

import java.math.BigDecimal;
import java.util.List;

import io.github.nguyennhatquang.fashion.common.Enum.ProductMediaTypeEnum;
import io.github.nguyennhatquang.fashion.common.Enum.ProductStatusEnum;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ProductRequest {
    // --- 1. Main Request DTOs ---

    public record ProductCreateRequest(
            @NotBlank(message = "Tên sản phẩm không được để trống") String name,

            @NotBlank(message = "Slug không được để trống") String slug,

            String brandId, // Chỉ nhận ID, không nhận Tên

            @NotEmpty(message = "Sản phẩm phải thuộc ít nhất 1 danh mục") List<String> categoryIds,

            String description,

            @NotNull(message = "Giá gốc không được để trống") @DecimalMin(value = "0.0", inclusive = false, message = "Giá sản phẩm phải lớn hơn 0") BigDecimal basePrice,

            // Sử dụng các Sub-DTO ngay bên dưới
            @Valid List<AttributeRequest> attributes,

            @Valid List<MediaRequest> media,

            ProductStatusEnum status,

            SeoRequest seo) {
    }

    public record ProductUpdateRequest(
            // Tương tự Create, bạn có thể copy các trường sang đây
            // hoặc bỏ đi các trường không cho phép sửa (như slug)
            @NotBlank(message = "Tên sản phẩm không được để trống") String name,

            String brandId,
            List<String> categoryIds,
            String description,
            BigDecimal basePrice,
            @Valid List<AttributeRequest> attributes,
            @Valid List<MediaRequest> media,
            ProductStatusEnum status,
            SeoRequest seo) {
    }

    // --- 2. Sub-DTOs (Dùng chung cho cả Create và Update) ---

    public record AttributeRequest(
            @NotBlank(message = "Tên thuộc tính không được để trống") String name,

            @NotEmpty(message = "Phải có ít nhất 1 tùy chọn cho thuộc tính") List<String> options) {
    }

    public record MediaRequest(
            @NotBlank(message = "URL media không được để trống") String url,

            @NotNull(message = "Loại media không được để trống") ProductMediaTypeEnum type,

            Boolean isThumbnail) {
    }

    public record SeoRequest(
            String metaTitle,
            String metaDescription) {
    }
}