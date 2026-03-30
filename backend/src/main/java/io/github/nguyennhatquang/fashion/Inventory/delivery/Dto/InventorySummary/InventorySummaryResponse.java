package io.github.nguyennhatquang.fashion.Inventory.delivery.Dto.InventorySummary;

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
public class InventorySummaryResponse {
    private UUID id;
    private UUID warehouseId;
    private String skuCode;
    private Integer onHand;
    private Integer reserved;
    /** Computed by DB: onHand - reserved */
    private Integer available;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}
