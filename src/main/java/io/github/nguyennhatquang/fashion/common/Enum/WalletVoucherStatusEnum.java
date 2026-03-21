package io.github.nguyennhatquang.fashion.common.Enum;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Represents the status of a voucher stored in a user's wallet.
 * PostgreSQL type: wallet_voucher_status_enum
 * JSON wire values: "collected" | "used" | "expired"
 */
public enum WalletVoucherStatusEnum {

    COLLECTED("collected"),
    USED("used"),
    EXPIRED("expired");

    private final String value;

    WalletVoucherStatusEnum(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static WalletVoucherStatusEnum fromValue(String value) {
        if (value == null || value.isBlank()) return null;
        for (WalletVoucherStatusEnum e : values()) {
            if (e.value.equalsIgnoreCase(value)) return e;
        }
        throw new IllegalArgumentException("Invalid value for WalletVoucherStatusEnum: '" + value + "'");
    }
}
