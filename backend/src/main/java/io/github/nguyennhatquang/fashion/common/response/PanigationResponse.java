package io.github.nguyennhatquang.fashion.common.response;

import java.util.List;

import org.apache.poi.ss.formula.functions.T;

import com.google.auto.value.AutoValue.Builder;

import lombok.Data;

@Data
@Builder
public class PanigationResponse {
    private String cursor; // Cursor để dùng cho lần gọi tiếp theo
    private Integer limit;
    private String sort;
    private Boolean hasNext;
    private Boolean hasPrevious;
    private List<T> data;
}
    