package io.github.nguyennhatquang.fashion.Catalog.delivery.Dto.SearchModel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchModelRequest {
    String keyword;
    String categoryId;
    Double minPrice;
    Double maxPrice;
    // Phân trang trực tiếp với giá trị mặc định
    @Builder.Default
    private Integer page = 1;

    @Builder.Default
    private Integer size = 20;
}
