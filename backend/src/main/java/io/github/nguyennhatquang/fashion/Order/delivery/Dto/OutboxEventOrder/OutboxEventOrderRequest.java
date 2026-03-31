package io.github.nguyennhatquang.fashion.Order.delivery.Dto.OutboxEventOrder;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.experimental.UtilityClass;

import io.github.nguyennhatquang.fashion.common.Enum.AggregateTypeEnum;
import io.github.nguyennhatquang.fashion.common.Enum.OutboxStatusEnum;

import java.time.LocalDateTime;
import java.util.Map;

@UtilityClass
public class OutboxEventOrderRequest {
    public record OutboxEventOrderCreateRequest(
        @NotNull(message = "Loại Aggregate không được để trống")
        AggregateTypeEnum aggregateType,

        @NotBlank(message = "ID Aggregate không được để trống")
        String aggregateId,

        @NotBlank(message = "Loại sự kiện không được để trống")
        String type,

        @NotNull(message = "Payload không được để trống")
        Map<String, Object> payload,

        String traceId,

        @NotNull(message = "Số lần thử lại không được để trống")
        Integer retryCount,

        String errorMessage,

        @NotNull(message = "Trạng thái Outbox không được để trống")
        OutboxStatusEnum status,

        LocalDateTime processedAt
    ) {}

    public record OutboxEventOrderUpdateRequest(
        @NotNull(message = "Loại Aggregate không được để trống")
        AggregateTypeEnum aggregateType,

        @NotBlank(message = "ID Aggregate không được để trống")
        String aggregateId,

        @NotBlank(message = "Loại sự kiện không được để trống")
        String type,

        @NotNull(message = "Payload không được để trống")
        Map<String, Object> payload,

        String traceId,

        @NotNull(message = "Số lần thử lại không được để trống")
        Integer retryCount,

        String errorMessage,

        @NotNull(message = "Trạng thái Outbox không được để trống")
        OutboxStatusEnum status,

        LocalDateTime processedAt
    ) {}
}
