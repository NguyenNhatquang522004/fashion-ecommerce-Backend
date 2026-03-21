package io.github.nguyennhatquang.fashion.common.Enum;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Represents the payment gateway provider used for a transaction.
 * PostgreSQL type: payment_provider_enum
 * JSON wire values: "vnpay" | "momo" | "stripe" | "cod"
 */
public enum PaymentProviderEnum {

    VNPAY("vnpay"),
    MOMO("momo"),
    STRIPE("stripe"),
    COD("cod");

    private final String value;

    PaymentProviderEnum(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static PaymentProviderEnum fromValue(String value) {
        if (value == null || value.isBlank()) return null;
        for (PaymentProviderEnum e : values()) {
            if (e.value.equalsIgnoreCase(value)) return e;
        }
        throw new IllegalArgumentException("Invalid value for PaymentProviderEnum: '" + value + "'");
    }
}
