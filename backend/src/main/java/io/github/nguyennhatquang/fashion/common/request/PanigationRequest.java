package io.github.nguyennhatquang.fashion.common.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PanigationRequest {
    private String cursor;
    private Integer limit = 10;
    private String sort = "DESC"; // "ASC" hoặc "DESC" dựa theo createdAt
    private Boolean hasNext = true; // true: Lấy trang tiếp theo (Cuộn xuống)
    private Boolean hasPrevious = false; // true: Lấy trang trước đó (Cuộn lên)
}
