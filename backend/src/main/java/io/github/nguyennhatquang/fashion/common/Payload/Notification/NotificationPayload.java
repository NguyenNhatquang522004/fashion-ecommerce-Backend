package io.github.nguyennhatquang.fashion.common.Payload.Notification;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

import com.drew.lang.annotations.NotNull;
import com.fasterxml.jackson.annotation.JsonFormat;

import io.github.nguyennhatquang.fashion.common.Enum.NotificationChannelEnum;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record NotificationPayload(
        // --- METADATA ---
        @NotNull UUID eventId,

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX") @NotNull OffsetDateTime timestamp,

        // --- DATA ---
        @NotNull UUID userId,

        @Email @NotBlank String customerEmail, // Chứa sẵn Email để Notification Service không cần query DB (Giảm tải)

        @NotBlank String customerName,

        @NotNull NotificationChannelEnum type, // Loại thông báo (ORDER_SUCCESS, ORDER_FAILED_OUT_OF_STOCK)

        @NotNull Map<String, Object> templateData // Dữ liệu linh hoạt đổ vào Template Email (Thường dùng Map để linh
                                                  // động)
) {

}
