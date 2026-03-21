package io.github.nguyennhatquang.fashion.common.Enum;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Represents the type of inventory movement recorded in the audit log (Cassandra).
 * JSON wire values: "stock_in" | "stock_out" | "reserve" | "release"
 */
public enum InventoryAuditTransactionType {

    STOCK_IN("stock_in"),
    STOCK_OUT("stock_out"),
    RESERVE("reserve"),
    RELEASE("release");

    private final String value;

    InventoryAuditTransactionType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static InventoryAuditTransactionType fromValue(String value) {
        if (value == null || value.isBlank()) return null;
        for (InventoryAuditTransactionType e : values()) {
            if (e.value.equalsIgnoreCase(value)) return e;
        }
        throw new IllegalArgumentException("Invalid value for InventoryAuditTransactionType: '" + value + "'");
    }
}
