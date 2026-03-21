package io.github.nguyennhatquang.fashion.common.Enum;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Represents the aggregate/domain an outbox event belongs to.
 * PostgreSQL type: aggregate_type_enum
 * JSON wire values: "order" | "inventory" | "payment" | "notification"
 */
public enum AggregateTypeEnum {

    ORDER("order"),
    INVENTORY("inventory"),
    PAYMENT("payment"),
    NOTIFICATION("notification");

    private final String value;

    AggregateTypeEnum(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static AggregateTypeEnum fromValue(String value) {
        if (value == null || value.isBlank()) return null;
        for (AggregateTypeEnum e : values()) {
            if (e.value.equalsIgnoreCase(value)) return e;
        }
        throw new IllegalArgumentException("Invalid value for AggregateTypeEnum: '" + value + "'");
    }
}
