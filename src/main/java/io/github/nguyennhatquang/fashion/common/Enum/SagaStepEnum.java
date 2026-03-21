package io.github.nguyennhatquang.fashion.common.Enum;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Represents individual steps within the order creation saga.
 * PostgreSQL type: saga_step_enum
 * JSON wire values: "reserve_inventory" | "process_payment" | "complete_order"
 */
public enum SagaStepEnum {

    RESERVE_INVENTORY("reserve_inventory"),
    PROCESS_PAYMENT("process_payment"),
    COMPLETE_ORDER("complete_order");

    private final String value;

    SagaStepEnum(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static SagaStepEnum fromValue(String value) {
        if (value == null || value.isBlank()) return null;
        for (SagaStepEnum e : values()) {
            if (e.value.equalsIgnoreCase(value)) return e;
        }
        throw new IllegalArgumentException("Invalid value for SagaStepEnum: '" + value + "'");
    }
}
