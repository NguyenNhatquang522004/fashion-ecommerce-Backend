package io.github.nguyennhatquang.fashion.common.Enum;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Represents the loyalty tier level of a user.
 * PostgreSQL type: loyalty_tier_enum
 * JSON wire values: "bronze" | "silver" | "gold" | "platinum"
 */
@Getter
@RequiredArgsConstructor
public enum LoyaltyTierEnum {

    BRONZE("bronze"),
    SILVER("silver"),
    GOLD("gold"),
    PLATINUM("platinum");

    private final String value;


    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static LoyaltyTierEnum fromValue(String value) {
        if (value == null || value.isBlank()) return null;
        for (LoyaltyTierEnum e : values()) {
            if (e.value.equalsIgnoreCase(value)) return e;
        }
        throw new IllegalArgumentException("Invalid value for LoyaltyTierEnum: '" + value + "'");
    }
}
