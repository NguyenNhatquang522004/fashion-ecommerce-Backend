package io.github.nguyennhatquang.fashion.Inventory.delivery.Dto.OutboxEventInventory;

import io.github.nguyennhatquang.fashion.common.Enum.OutboxStatusEnum;
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
public class OutboxEventInventoryResponse {
    private UUID id;
    private String aggregateType;
    private String aggregateId;
    private String type;
    private String payload;
    private OutboxStatusEnum status;
    private OffsetDateTime processedAt;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}
