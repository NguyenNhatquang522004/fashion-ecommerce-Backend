package io.github.nguyennhatquang.fashion.Catalog.delivery.Dto.SkuVariant;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import io.github.nguyennhatquang.fashion.Catalog.delivery.Dto.SkuVariant.SkuVariantRequest.SkuAttributeRequest;

public record SkuVariantResponse(
        String id,
        String productId,
        String skuCode,
        String barcode,
        List<SkuAttributeRequest> attributes,
        BigDecimal priceOverride,
        List<String> media,
        Boolean isActive,

        // Trả về Audit fields cho Admin
        Instant createdAt,
        Instant updatedAt,
        String createdBy,
        String updatedBy) {
}