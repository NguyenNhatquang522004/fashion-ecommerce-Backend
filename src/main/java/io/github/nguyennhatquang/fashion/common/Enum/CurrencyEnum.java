package io.github.nguyennhatquang.fashion.common.Enum;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Represents the currency used in payment transactions.
 * PostgreSQL type: currency_enum
 * JSON wire values: "vnd" | "usd"
 */
public enum CurrencyEnum {

    VND("vnd"),
    USD("usd");

    private final String value;

    CurrencyEnum(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static CurrencyEnum fromValue(String value) {
        if (value == null || value.isBlank()) return null;
        for (CurrencyEnum e : values()) {
            if (e.value.equalsIgnoreCase(value)) return e;
        }
        throw new IllegalArgumentException("Invalid value for CurrencyEnum: '" + value + "'");
    }
}
