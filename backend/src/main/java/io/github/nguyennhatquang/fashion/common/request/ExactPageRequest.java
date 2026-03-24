package io.github.nguyennhatquang.fashion.common.request;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class ExactPageRequest {
    private int page = 1; // Trang bắt đầu từ 1 cho thân thiện với Client
    private int limit = 10;

    // Client gửi lên từ lần gọi thứ 2 trở đi để giữ nguyên timeline
    private LocalDateTime snapshotTime;
}