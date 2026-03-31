package io.github.nguyennhatquang.fashion.common.Payload.order;

import java.time.OffsetDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.github.nguyennhatquang.fashion.common.Enum.CancelReasonEnum;
import jakarta.validation.constraints.NotNull;

public record OrderCancelledPayload(
        // --- METADATA ---
        @NotNull UUID eventId,
        @NotNull UUID correlationId,

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX") @NotNull OffsetDateTime timestamp,

        // --- DATA ---
        @NotNull UUID orderId, // Inventory Service sẽ dùng orderId này để query: SELECT * FROM
                               // stock_reservations WHERE order_id = ?

        @NotNull CancelReasonEnum reason // Lý do hủy để Inventory ghi vào cột 'note' của bảng Sổ cái (Ledger)
) {

}
