package io.github.nguyennhatquang.fashion.common.Payload.inventory;

import java.time.OffsetDateTime;
import java.util.UUID;

import io.github.nguyennhatquang.fashion.common.Enum.ReservationStatusEnum;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockReservationCreatePayload {
    @NotBlank(message = "Order ID không được để trống")
    String orderId;

    @NotBlank(message = "SKU code không được để trống")
    String skuCode;

    @NotNull(message = "Warehouse ID là bắt buộc")
    UUID warehouseId;
    @NotNull(message = "Trạng thái là bắt buộc")
    ReservationStatusEnum status;

    @NotNull(message = "Số lượng là bắt buộc")
    @Min(value = 1, message = "Số lượng phải ít nhất là 1")
    Integer quantity;

    @NotNull(message = "Thời hạn hết hiệu lực là bắt buộc")
    OffsetDateTime expiresAt = OffsetDateTime.now().plusMinutes(5);
}
