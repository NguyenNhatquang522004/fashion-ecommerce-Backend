package io.github.nguyennhatquang.fashion.common.response;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExactPageResponse<T> {
    private int currentPage;
    private int totalPages;
    private long totalElements;
    private LocalDateTime snapshotTime; // Client cần lưu lại giá trị này
    private List<T> data;
}