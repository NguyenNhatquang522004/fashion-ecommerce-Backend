package io.github.nguyennhatquang.fashion.Order.delivery.Dto.OutboxEventOrder;

import lombok.Builder;
import lombok.Value;

import io.github.nguyennhatquang.fashion.common.Enum.AggregateTypeEnum;
import io.github.nguyennhatquang.fashion.common.Enum.OutboxStatusEnum;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Value
@Builder
public class OutboxEventOrderResponse {
    UUID id;
    AggregateTypeEnum aggregateType;
    String aggregateId;
    String type;
    Map<String, Object> payload;
    String traceId;
    Integer retryCount;
    String errorMessage;
    OutboxStatusEnum status;
    LocalDateTime processedAt;
    String createdBy;
    LocalDateTime createdAt;
    String updatedBy;
    LocalDateTime updatedAt;
}
