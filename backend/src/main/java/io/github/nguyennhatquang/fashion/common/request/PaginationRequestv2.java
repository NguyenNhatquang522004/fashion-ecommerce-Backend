package io.github.nguyennhatquang.fashion.common.request;

import java.util.Map;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaginationRequestv2 {
    private String cursor;
    private Integer limit = 10;

    // Khách hàng muốn sort theo trường nào? (Mặc định là createdAt)
    @Builder.Default
    private String sortBy = "createdAt";

    // Chiều sắp xếp: "ASC" hoặc "DESC"
    @Builder.Default
    private String sortDirection = "DESC";

    private Boolean hasNext = true;
    private Boolean hasPrevious = false;


    private Map<String, Object> filters;
}
