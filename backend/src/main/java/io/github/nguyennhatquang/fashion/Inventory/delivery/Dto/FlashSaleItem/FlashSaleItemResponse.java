package io.github.nguyennhatquang.fashion.Inventory.delivery.Dto.FlashSaleItem;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlashSaleItemResponse {
    private UUID id;
    private UUID campaignId;
    private String skuCode;
    private BigDecimal promotionalPrice;
    private Integer totalQuota;
    private Integer purchaseLimitPerUser;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}
