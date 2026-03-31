package io.github.nguyennhatquang.fashion.Order.delivery.Dto.SagaState;

import lombok.Builder;
import lombok.Value;

import io.github.nguyennhatquang.fashion.common.Enum.SagaStatusEnum;
import io.github.nguyennhatquang.fashion.common.Enum.SagaStepEnum;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Value
@Builder
public class SagaStateResponse {
    UUID id;
    UUID orderId;
    SagaStepEnum currentStep;
    SagaStatusEnum status;
    Map<String, Object> payload;
    String createdBy;
    LocalDateTime createdAt;
    String updatedBy;
    LocalDateTime updatedAt;
}
