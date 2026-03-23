package io.github.nguyennhatquang.fashion.common.Enum;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Represents the status of a payment transaction.
 * PostgreSQL type: payment_status_enum
 * JSON wire values: "pending" | "success" | "failed" | "refunded"
 */
@Getter
@RequiredArgsConstructor
public enum PaymentStatusEnum {

    PENDING("pending"),
    SUCCESS("success"),
    FAILED("failed"),
    REFUNDED("refunded");

    private final String value;


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
