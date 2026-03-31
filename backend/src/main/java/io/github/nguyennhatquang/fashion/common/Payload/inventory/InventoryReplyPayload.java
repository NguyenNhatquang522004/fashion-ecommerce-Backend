package io.github.nguyennhatquang.fashion.common.Payload.inventory;

import java.time.OffsetDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.github.nguyennhatquang.fashion.common.Enum.ReplyStatusEnum;
import jakarta.validation.constraints.NotNull;

public record InventoryReplyPayload(
        // --- METADATA ---
        @NotNull UUID eventId,
        @NotNull UUID correlationId, // BẮT BUỘC: Trả lại đúng correlationId nhận được từ OrderCreatedEvent

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX") @NotNull OffsetDateTime timestamp,

        // --- DATA ---
        @NotNull UUID orderId,

        @NotNull ReplyStatusEnum status, // Trạng thái trả về (SUCCESS hoặc FAILED)

        String errorMessage) {
}
