package io.github.nguyennhatquang.fashion.Inventory.delivery.Dto.OutboxEventInventory;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.experimental.UtilityClass;

import io.github.nguyennhatquang.fashion.common.Enum.OutboxStatusEnum;

@UtilityClass
public class OutboxEventInventoryRequest {

    /**
     * OutboxEventInventory thường được tạo nội bộ (internal domain event),
     * nhưng vẫn cần CreateRequest cho admin/manual trigger.
     */
    public record OutboxEventInventoryCreateRequest(
            @NotBlank(message = "Aggregate type không được để trống")
            String aggregateType,

            @NotBlank(message = "Aggregate ID không được để trống")
            String aggregateId,

            @NotBlank(message = "Loại event không được để trống")
            String type,

            @NotBlank(message = "Payload không được để trống")
            String payload
    ) {}

    /**
     * Chỉ cho phép cập nhật trạng thái (ví dụ: PENDING → PUBLISHED / FAILED).
     */
    public record OutboxEventInventoryUpdateRequest(
            @NotNull(message = "Trạng thái là bắt buộc")
            OutboxStatusEnum status
    ) {}
}
