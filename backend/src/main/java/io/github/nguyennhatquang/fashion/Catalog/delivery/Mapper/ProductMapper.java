package io.github.nguyennhatquang.fashion.Catalog.delivery.Mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import io.github.nguyennhatquang.fashion.Catalog.delivery.Dto.Product.ProductResponse;
import io.github.nguyennhatquang.fashion.Catalog.delivery.Dto.Product.ProductRequest.ProductCreateRequest;
import io.github.nguyennhatquang.fashion.Catalog.delivery.Dto.Product.ProductRequest.ProductUpdateRequest;
import io.github.nguyennhatquang.fashion.Catalog.domain.entity.Product;
@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface ProductMapper {
    // 1. Map từ CreateRequest sang Entity
    @Mapping(target = "id", ignore = true)
    // NGĂN CHẶN giả mạo brandName: Server phải tự xử lý thông qua brandId
    @Mapping(target = "brandName", ignore = true) 
    @Mapping(target = "isDeleted", constant = "false")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    Product toEntity(ProductCreateRequest request);

    // 2. Map từ UpdateRequest để cập nhật Entity
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "brandName", ignore = true) // Cấm Client tự sửa brandName
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    void updateEntityFromRequest(ProductUpdateRequest request, @MappingTarget Product product);

    // 3. Map từ Entity ra Response
    ProductResponse toResponse(Product product);

    // 4. Map List
    List<ProductResponse> toResponseList(List<Product> products);
}
