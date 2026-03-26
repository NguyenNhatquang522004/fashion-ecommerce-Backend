package io.github.nguyennhatquang.fashion.Catalog.delivery.Dto.Brand;

import java.time.Instant;

public class BrandResponse {
    String id;
    String name;
    String slug;
    String logoUrl;
    String description;
    Boolean isActive;
    Instant createdAt;
    Instant updatedAt;
    String createdBy;
    String updatedBy;
}
