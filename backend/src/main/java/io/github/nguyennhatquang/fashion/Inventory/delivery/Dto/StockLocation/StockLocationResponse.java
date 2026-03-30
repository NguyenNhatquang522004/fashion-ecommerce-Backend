package io.github.nguyennhatquang.fashion.Inventory.delivery.Dto.StockLocation;

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
public class StockLocationResponse {
    private UUID id;
    private UUID warehouseId;
    private String zone;
    private String aisle;
    private String rack;
    private String shelf;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}
