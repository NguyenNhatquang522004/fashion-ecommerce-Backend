package io.github.nguyennhatquang.fashion.Inventory.delivery.Dto.InventoryLedger;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.experimental.UtilityClass;

import io.github.nguyennhatquang.fashion.common.Enum.InventoryTransactionTypeEnum;

import java.util.UUID;

@UtilityClass
public class InventoryLedgerRequest {

    /**
     * InventoryLedger là append-only (immutable), chỉ cần CreateRequest.
     * Không có UpdateRequest vì đây là bảng audit tài chính.
     */
    @Builder
    public record InventoryLedgerCreateRequest(
            @NotNull(message = "Warehouse ID là bắt buộc") UUID warehouseId,

            @NotBlank(message = "SKU code không được để trống") String skuCode,

            @NotNull(message = "Loại giao dịch là bắt buộc") InventoryTransactionTypeEnum transactionType,

            @NotNull(message = "Số lượng thay đổi là bắt buộc") Integer quantityChange,

            String referenceId,

            String note) {
    }
}
