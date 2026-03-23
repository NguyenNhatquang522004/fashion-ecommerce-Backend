package io.github.nguyennhatquang.fashion.common.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SystemRes {
    private String message;
    private String code;
    private String status;
    private Object data;

    public static SystemRes success(String message, Object data) {
        return SystemRes.builder()
                .message(message)
                .code("200")
                .status("success")
                .data(data)
                .build();
    }

    public static SystemRes error(String message, String code) {
        return SystemRes.builder()
                .message(message)
                .code(code)
                .status("error")
                .build();
    }
}
