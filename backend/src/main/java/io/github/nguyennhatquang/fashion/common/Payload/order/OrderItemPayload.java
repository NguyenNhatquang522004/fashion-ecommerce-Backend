package io.github.nguyennhatquang.fashion.common.Payload.order;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;

public record OrderItemPayload(
        @NotNull UUID warehouseId, // Cần biết giữ ở kho nào
        @NotNull String skuCode, // Mã sản phẩm (VD: SHIRT-RED-L)
        @NotNull Integer quantity // Số lượng cần giữ
) {
}
