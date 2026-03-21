package io.github.nguyennhatquang.fashion.common.Enum;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Represents the lifecycle status of a discount voucher.
 * PostgreSQL type: voucher_status_enum
 * JSON wire values: "active" | "inactive" | "expired"
 */
@Getter
@RequiredArgsConstructor
public enum VoucherStatusEnum {

    ACTIVE("active"),
    INACTIVE("inactive"),
    EXPIRED("expired");

    private final String value;



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
