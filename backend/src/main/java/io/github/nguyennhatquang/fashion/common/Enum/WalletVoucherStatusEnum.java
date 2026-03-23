package io.github.nguyennhatquang.fashion.common.Enum;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Represents the status of a voucher stored in a user's wallet.
 * PostgreSQL type: wallet_voucher_status_enum
 * JSON wire values: "collected" | "used" | "expired"
 */
@Getter
@RequiredArgsConstructor
public enum WalletVoucherStatusEnum {

    COLLECTED("collected"),
    USED("used"),
    EXPIRED("expired");

    private final String value;

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static WalletVoucherStatusEnum fromValue(String value) {
        if (value == null || value.isBlank())
            return null;
        for (WalletVoucherStatusEnum e : values()) {
            if (e.value.equalsIgnoreCase(value))
                return e;
        }
        throw new IllegalArgumentException("Invalid value for WalletVoucherStatusEnum: '" + value + "'");
    }
}
