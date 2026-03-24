package io.github.nguyennhatquang.fashion.common.response;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PanigationResponse<T> {
    private String cursor; // Cursor để dùng cho lần gọi tiếp theo
    private Integer limit;
    private String sort;
    private Boolean hasNext;
    private Boolean hasPrevious;
    private List<T> data;
}
