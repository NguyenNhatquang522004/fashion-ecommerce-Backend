package io.github.nguyennhatquang.fashion.Order.delivery.Dto.Shipment;

import lombok.Builder;
import lombok.Value;

import io.github.nguyennhatquang.fashion.common.Enum.ShipmentProviderEnum;
import io.github.nguyennhatquang.fashion.common.Enum.ShipmentStatusEnum;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Value
@Builder
public class ShipmentResponse {
    UUID id;
    UUID orderId;
    String trackingCode;
    ShipmentProviderEnum provider;
    ShipmentStatusEnum status;
    LocalDate estimatedDeliveryDate;
    LocalDateTime actualDeliveryDate;
    String createdBy;
    LocalDateTime createdAt;
    String updatedBy;
    LocalDateTime updatedAt;
}
