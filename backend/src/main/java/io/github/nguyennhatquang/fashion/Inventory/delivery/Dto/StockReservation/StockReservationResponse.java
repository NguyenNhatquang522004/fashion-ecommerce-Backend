package io.github.nguyennhatquang.fashion.Inventory.delivery.Dto.StockReservation;

import io.github.nguyennhatquang.fashion.common.Enum.ReservationStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockReservationResponse {
    private UUID id;
    private String orderId;
    private String skuCode;
    private UUID warehouseId;
    private Integer quantity;
    private ReservationStatusEnum status;
    private OffsetDateTime expiresAt;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}
