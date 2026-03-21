package io.github.nguyennhatquang.fashion.common.Enum;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Represents the lifecycle status of a discount voucher.
 * PostgreSQL type: voucher_status_enum
 * JSON wire values: "active" | "inactive" | "expired"
 */
public enum VoucherStatusEnum {

    ACTIVE("active"),
    INACTIVE("inactive"),
    EXPIRED("expired");

    private final String value;

    VoucherStatusEnum(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static VoucherStatusEnum fromValue(String value) {
        if (value == null || value.isBlank()) return null;
        for (VoucherStatusEnum e : values()) {
            if (e.value.equalsIgnoreCase(value)) return e;
        }
        throw new IllegalArgumentException("Invalid value for VoucherStatusEnum: '" + value + "'");
    }
}
