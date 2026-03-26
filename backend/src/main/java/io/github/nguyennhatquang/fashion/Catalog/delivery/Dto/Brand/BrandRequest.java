package io.github.nguyennhatquang.fashion.Catalog.delivery.Dto.Brand;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.experimental.UtilityClass;

@UtilityClass
public class BrandRequest {
     public record BrandCreateRequest(
        @NotBlank(message = "Tên thương hiệu không được để trống")
        String name,

        @NotBlank(message = "Slug không được để trống")
        @Pattern(regexp = "^[a-z0-9-]+$", message = "Slug chỉ hợp lệ với chữ cái thường, số và dấu gạch ngang (-)")
        String slug,

        String logoUrl,
        
        String description,

        @NotNull(message = "Trạng thái is_active là bắt buộc")
        Boolean isActive
     ) {}

     public record  BrandUpdateRequest(
        @NotBlank(message = "Tên thương hiệu không được để trống")
        String name,

        @NotBlank(message = "Slug không được để trống")
        @Pattern(regexp = "^[a-z0-9-]+$", message = "Slug chỉ hợp lệ với chữ cái thường, số và dấu gạch ngang (-)")
        String slug,

        String logoUrl,
        
        String description,

        @NotNull(message = "Trạng thái is_active là bắt buộc")
        Boolean isActive
     ) {}
}
