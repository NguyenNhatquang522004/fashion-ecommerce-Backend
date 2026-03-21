package io.github.nguyennhatquang.fashion.common.Enum;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Represents the status of a payment transaction.
 * PostgreSQL type: payment_status_enum
 * JSON wire values: "pending" | "success" | "failed" | "refunded"
 */
public enum PaymentStatusEnum {

    PENDING("pending"),
    SUCCESS("success"),
    FAILED("failed"),
    REFUNDED("refunded");

    private final String value;

    PaymentStatusEnum(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static PaymentStatusEnum fromValue(String value) {
        if (value == null || value.isBlank()) return null;
        for (PaymentStatusEnum e : values()) {
            if (e.value.equalsIgnoreCase(value)) return e;
        }
        throw new IllegalArgumentException("Invalid value for PaymentStatusEnum: '" + value + "'");
    }
}
