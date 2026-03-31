package io.github.nguyennhatquang.fashion.common.Payload.order;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record OrderCreatedPayload(
        // --- METADATA ---
        @NotNull UUID eventId, // ID duy nhất của Kafka message (Dùng để check Idempotency)
        @NotNull UUID correlationId, // Trace ID xuyên suốt các microservices (Thường dùng OrderId hoặc SagaId)

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX") @NotNull OffsetDateTime timestamp, // Thời gian sinh event

        // --- DATA ---
        @NotNull UUID orderId, // ID đơn hàng
        @NotNull UUID userId, // ID khách hàng
        @NotEmpty List<OrderItemPayload> items // Danh sách SKU cần giữ kho
) {
}