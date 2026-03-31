package io.github.nguyennhatquang.fashion.Order.delivery.Dto.Shipment;

import jakarta.validation.constraints.NotNull;
import lombok.experimental.UtilityClass;

import io.github.nguyennhatquang.fashion.common.Enum.ShipmentProviderEnum;
import io.github.nguyennhatquang.fashion.common.Enum.ShipmentStatusEnum;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@UtilityClass
public class ShipmentRequest {
    public record ShipmentCreateRequest(
        @NotNull(message = "Order ID không được để trống")
        UUID orderId,

        String trackingCode,

        @NotNull(message = "Provider không được để trống")
        ShipmentProviderEnum provider,

        @NotNull(message = "Status không được để trống")
        ShipmentStatusEnum status,

        LocalDate estimatedDeliveryDate,
        
        LocalDateTime actualDeliveryDate
    ) {}

    public record ShipmentUpdateRequest(
        @NotNull(message = "Order ID không được để trống")
        UUID orderId,

        String trackingCode,

        @NotNull(message = "Provider không được để trống")
        ShipmentProviderEnum provider,

        @NotNull(message = "Status không được để trống")
        ShipmentStatusEnum status,

        LocalDate estimatedDeliveryDate,
        
        LocalDateTime actualDeliveryDate
    ) {}
}
