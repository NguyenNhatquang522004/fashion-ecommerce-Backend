package io.github.nguyennhatquang.fashion.common.Enum;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Represents the type of discount applied to a voucher or campaign.
 * PostgreSQL type: discount_type_enum
 * JSON wire values: "percentage" | "fixed_amount"
 */
@Getter
@RequiredArgsConstructor
public enum DiscountTypeEnum {

    PERCENTAGE("percentage"),
    FIXED_AMOUNT("fixed_amount");

    private final String value;

 
    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static DiscountTypeEnum fromValue(String value) {
        if (value == null || value.isBlank()) return null;
        for (DiscountTypeEnum e : values()) {
            if (e.value.equalsIgnoreCase(value)) return e;
        }
        throw new IllegalArgumentException("Invalid value for DiscountTypeEnum: '" + value + "'");
    }
}
