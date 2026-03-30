package io.github.nguyennhatquang.fashion.Inventory.delivery.Dto.StockReservation;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.experimental.UtilityClass;

import io.github.nguyennhatquang.fashion.common.Enum.ReservationStatusEnum;

import java.time.OffsetDateTime;
import java.util.UUID;

@UtilityClass
public class StockReservationRequest {

    public record StockReservationCreateRequest(
            @NotBlank(message = "Order ID không được để trống")
            String orderId,

            @NotBlank(message = "SKU code không được để trống")
            String skuCode,

            @NotNull(message = "Warehouse ID là bắt buộc")
            UUID warehouseId,

            @NotNull(message = "Số lượng là bắt buộc")
            @Min(value = 1, message = "Số lượng phải ít nhất là 1")
            Integer quantity,

            @NotNull(message = "Thời hạn hết hiệu lực là bắt buộc")
            OffsetDateTime expiresAt
    ) {}

    public record StockReservationUpdateRequest(
            @NotNull(message = "Trạng thái là bắt buộc")
            ReservationStatusEnum status,

            OffsetDateTime expiresAt
    ) {}
}
