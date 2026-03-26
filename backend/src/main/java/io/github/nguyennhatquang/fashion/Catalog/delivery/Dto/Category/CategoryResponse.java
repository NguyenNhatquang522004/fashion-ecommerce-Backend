package io.github.nguyennhatquang.fashion.Catalog.delivery.Dto.Category;

import java.time.Instant;

public record CategoryResponse(
        String id,
        String name,
        String slug,
        String parentId, // Rất quan trọng để Client biết ai là cha của nó
        String path, // Dùng để query hoặc build Breadcrumb (Ví dụ: Trang chủ > Thời trang nam > Áo
                     // thun)
        String imageUrl,
        Boolean isActive,
        Integer sortOrder,
        // Audit fields thường được dùng cho màn hình Admin
        Instant createdAt,
        Instant updatedAt,
        String createdBy,
        String updatedBy) {
}
