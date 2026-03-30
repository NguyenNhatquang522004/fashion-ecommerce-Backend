package io.github.nguyennhatquang.fashion.Inventory.delivery.Dto.StockLocation;

import jakarta.validation.constraints.NotNull;
import lombok.experimental.UtilityClass;

import java.util.UUID;

@UtilityClass
public class StockLocationRequest {

    public record StockLocationCreateRequest(
            @NotNull(message = "Warehouse ID là bắt buộc")
            UUID warehouseId,

            String zone,
            String aisle,
            String rack,
            String shelf
    ) {}

    public record StockLocationUpdateRequest(
            String zone,
            String aisle,
            String rack,
            String shelf
    ) {}
}
