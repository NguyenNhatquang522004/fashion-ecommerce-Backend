package io.github.nguyennhatquang.fashion.Inventory.delivery.Dto.InventoryLedger;

import io.github.nguyennhatquang.fashion.common.Enum.InventoryTransactionTypeEnum;
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
public class InventoryLedgerResponse {
    private UUID id;
    private UUID warehouseId;
    private String skuCode;
    private InventoryTransactionTypeEnum transactionType;
    private Integer quantityChange;
    private String referenceId;
    private String note;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}
