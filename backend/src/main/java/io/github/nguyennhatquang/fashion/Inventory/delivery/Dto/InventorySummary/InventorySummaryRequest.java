package io.github.nguyennhatquang.fashion.Inventory.delivery.Dto.InventorySummary;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.experimental.UtilityClass;

import java.util.UUID;

@UtilityClass
public class InventorySummaryRequest {

    public record InventorySummaryCreateRequest(
            @NotNull(message = "Warehouse ID là bắt buộc")
            UUID warehouseId,

            @NotBlank(message = "SKU code không được để trống")
            String skuCode,

            @Min(value = 0, message = "Số lượng tồn kho không được âm")
            Integer onHand,

            @Min(value = 0, message = "Số lượng đặt trước không được âm")
            Integer reserved
    ) {}

    public record InventorySummaryUpdateRequest(
            @NotNull(message = "Số lượng tồn kho là bắt buộc")
            @Min(value = 0, message = "Số lượng tồn kho không được âm")
            Integer onHand,

            @NotNull(message = "Số lượng đặt trước là bắt buộc")
            @Min(value = 0, message = "Số lượng đặt trước không được âm")
            Integer reserved
    ) {}
}
