package io.github.nguyennhatquang.fashion.common.request;

import java.time.LocalDateTime;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExactPageRequestv2 {
    @Builder.Default
    private int page = 1; // Trang bắt đầu từ 1

    @Builder.Default
    private int limit = 10;

    // Snapshot time để giữ nguyên timeline, chống trôi trang (Drift Pagination)
    private LocalDateTime snapshotTime;

    // --- CÁC TRƯỜNG DYNAMIC MỚI ---
    @Builder.Default
    private String sortBy = "createdAt"; // Mặc định sort theo thời gian tạo

    @Builder.Default
    private String sortDirection = "DESC"; // "ASC" hoặc "DESC"

    // Hỗ trợ Client filter động (VD: {"status": "ACTIVE", "brandId": "123"})
    private Map<String, Object> filters;
}
