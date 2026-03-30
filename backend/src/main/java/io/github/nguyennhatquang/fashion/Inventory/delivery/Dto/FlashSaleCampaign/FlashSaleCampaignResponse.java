package io.github.nguyennhatquang.fashion.Inventory.delivery.Dto.FlashSaleCampaign;

import io.github.nguyennhatquang.fashion.common.Enum.CampaignStatusEnum;
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
public class FlashSaleCampaignResponse {
    private UUID id;
    private String name;
    private OffsetDateTime startTime;
    private OffsetDateTime endTime;
    private CampaignStatusEnum status;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}
