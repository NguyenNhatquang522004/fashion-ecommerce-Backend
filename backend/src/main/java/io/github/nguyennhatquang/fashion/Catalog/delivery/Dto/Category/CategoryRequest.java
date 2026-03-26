package io.github.nguyennhatquang.fashion.Catalog.delivery.Dto.Category;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.experimental.UtilityClass;

@UtilityClass
public class CategoryRequest {
    public record CategoryCreateRequest(
        @NotBlank(message = "Tên danh mục không được để trống")
        String name,

        @NotBlank(message = "Slug không được để trống")
        @Pattern(regexp = "^[a-z0-9-]+$", message = "Slug chỉ hợp lệ với chữ cái thường, số và dấu gạch ngang (-)")
        String slug,

        // Có thể null nếu đây là danh mục gốc (Root Category)
        String parentId,

        String imageUrl,

        @NotNull(message = "Trạng thái is_active là bắt buộc")
        Boolean isActive,

        @Min(value = 0, message = "Thứ tự sắp xếp phải lớn hơn hoặc bằng 0")
        Integer sortOrder
    ) {
    }
    
    public record  CategoryUpdateRequest(
        @NotBlank(message = "Tên danh mục không được để trống")
        String name,

        @NotBlank(message = "Slug không được để trống")
        @Pattern(regexp = "^[a-z0-9-]+$", message = "Slug chỉ hợp lệ với chữ cái thường, số và dấu gạch ngang (-)")
        String slug,

        // Thay đổi cha (Di chuyển danh mục)
        String parentId,

        String imageUrl,

        @NotNull(message = "Trạng thái is_active là bắt buộc")
        Boolean isActive,

        @Min(value = 0, message = "Thứ tự sắp xếp phải lớn hơn hoặc bằng 0")
        Integer sortOrder
    ) { }
}
