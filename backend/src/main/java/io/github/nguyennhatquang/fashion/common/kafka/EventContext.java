package io.github.nguyennhatquang.fashion.common.kafka;

import io.github.nguyennhatquang.fashion.common.errors.ContextTimeoutException;

public record EventContext(
        String correlationId, // Dùng để trace toàn bộ flow của 1 request
        String userId, // Người dùng thực hiện action
        String traceId, // Tích hợp với OpenTelemetry/Zipkin nếu cần
        long timeoutMs // (Tùy chọn) Giới hạn thời gian xử lý
) {
    // Helper method tạo context mới cho entry point (ví dụ: REST Controller)
    public static EventContext generate(String userId) {
        String id = java.util.UUID.randomUUID().toString();
        return new EventContext(id, userId, id, 5000L);
    }

    public static EventContext generate(String userId, long timeoutMs) {
        String id = java.util.UUID.randomUUID().toString();
        return new EventContext(id, userId, id, timeoutMs);
    }

    // Kiểm tra xem đã hết giờ chưa (Giống ctx.Err() trong Go)
    public boolean isExpired() {
        return java.time.Instant.now().isAfter(java.time.Instant.ofEpochMilli(timeoutMs));
    }

    public long getRemainingMillis() {
        long remaining = java.time.Duration.between(java.time.Instant.now(), java.time.Instant.ofEpochMilli(timeoutMs))
                .toMillis();
        return Math.max(0, remaining);
    }

    // Method ném lỗi nếu quá hạn, dùng để check tại các chặng (checkpoints)
    public void throwIfExpired() {
        if (isExpired()) {
            throw new ContextTimeoutException("Execution deadline exceeded for trace: " + correlationId);
        }
    }
}