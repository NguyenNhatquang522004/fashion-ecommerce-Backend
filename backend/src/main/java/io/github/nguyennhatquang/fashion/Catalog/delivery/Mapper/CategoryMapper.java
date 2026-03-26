package io.github.nguyennhatquang.fashion.Catalog.delivery.Mapper;

import java.util.List;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import io.github.nguyennhatquang.fashion.Catalog.delivery.Dto.Category.CategoryRequest.CategoryCreateRequest;
import io.github.nguyennhatquang.fashion.Catalog.delivery.Dto.Category.CategoryRequest.CategoryUpdateRequest;
import io.github.nguyennhatquang.fashion.Catalog.domain.entity.Category;
import io.github.nguyennhatquang.fashion.Catalog.delivery.Dto.Category.CategoryResponse;

@Mapper(componentModel = "spring",
        // Bỏ qua map null đè lên dữ liệu có sẵn khi Update
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        // Không báo lỗi warning với các trường không được map
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CategoryMapper {
    // 1. Map từ CreateRequest sang Entity
    @Mapping(target = "id", ignore = true)
    // Path KHÔNG map ở đây. Phải tự set bằng logic trong Service (vd:
    // parent.getPath() + "," + entity.getId())
    @Mapping(target = "path", ignore = true)
    @Mapping(target = "isDeleted", constant = "false") // Đảm bảo luôn false khi tạo mới
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    Category toEntity(CategoryCreateRequest request);

    // 2. Map từ UpdateRequest để cập nhật Entity
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "path", ignore = true) // Cực kỳ quan trọng: Không cho Client tự sửa path
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    void updateEntityFromRequest(CategoryUpdateRequest request, @MappingTarget Category category);

    CategoryResponse toResponse(Category category);

    List<CategoryResponse> toResponseList(List<Category> categories);
}
