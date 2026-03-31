package io.github.nguyennhatquang.fashion.Order.delivery.Dto.SagaState;

import jakarta.validation.constraints.NotNull;
import lombok.experimental.UtilityClass;

import io.github.nguyennhatquang.fashion.common.Enum.SagaStatusEnum;
import io.github.nguyennhatquang.fashion.common.Enum.SagaStepEnum;

import java.util.Map;
import java.util.UUID;

@UtilityClass
public class SagaStateRequest {
    public record SagaStateCreateRequest(
        @NotNull(message = "Order ID không được để trống")
        UUID orderId,

        @NotNull(message = "Current Step không được để trống")
        SagaStepEnum currentStep,

        @NotNull(message = "Status không được để trống")
        SagaStatusEnum status,

        Map<String, Object> payload
    ) {}

    public record SagaStateUpdateRequest(
        @NotNull(message = "Order ID không được để trống")
        UUID orderId,

        @NotNull(message = "Current Step không được để trống")
        SagaStepEnum currentStep,

        @NotNull(message = "Status không được để trống")
        SagaStatusEnum status,

        Map<String, Object> payload
    ) {}
}
