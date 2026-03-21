package io.github.nguyennhatquang.fashion.common.Enum;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Represents the type of inventory stock movement.
 * PostgreSQL type: inventory_transaction_type_enum
 * JSON wire values: "stock_in" | "stock_out" | "reserve" | "release"
 */
public enum InventoryTransactionTypeEnum {

    STOCK_IN("stock_in"),
    STOCK_OUT("stock_out"),
    RESERVE("reserve"),
    RELEASE("release");

    private final String value;

    InventoryTransactionTypeEnum(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static InventoryTransactionTypeEnum fromValue(String value) {
        if (value == null || value.isBlank()) return null;
        for (InventoryTransactionTypeEnum e : values()) {
            if (e.value.equalsIgnoreCase(value)) return e;
        }
        throw new IllegalArgumentException("Invalid value for InventoryTransactionTypeEnum: '" + value + "'");
    }
}
