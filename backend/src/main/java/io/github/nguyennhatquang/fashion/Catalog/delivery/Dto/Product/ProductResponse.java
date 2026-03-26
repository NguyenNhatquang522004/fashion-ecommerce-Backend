package io.github.nguyennhatquang.fashion.Catalog.delivery.Dto.Product;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import io.github.nguyennhatquang.fashion.Catalog.delivery.Dto.Product.ProductRequest.AttributeRequest;
import io.github.nguyennhatquang.fashion.Catalog.delivery.Dto.Product.ProductRequest.MediaRequest;
import io.github.nguyennhatquang.fashion.Catalog.delivery.Dto.Product.ProductRequest.SeoRequest;
import io.github.nguyennhatquang.fashion.common.Enum.ProductStatusEnum;

public record ProductResponse(
        String id,
        String name,
        String slug,
        String brandId,
        String brandName, // Trả về cho Frontend hiển thị mà không cần join thêm
        List<String> categoryIds,
        String description,
        BigDecimal basePrice,
        List<AttributeRequest> attributes,
        List<MediaRequest> media,
        ProductStatusEnum status,
        SeoRequest seo,

        // Audit fields
        Instant createdAt,
        Instant updatedAt,
        String createdBy,
        String updatedBy) {

}
