package io.github.nguyennhatquang.fashion.common.Enum;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Represents the status of a user profile account.
 * PostgreSQL type: profile_status_enum
 * JSON wire values: "active" | "inactive" | "banned"
 */
@Getter
@RequiredArgsConstructor
public enum ProfileStatusEnum {

    ACTIVE("active"),
    INACTIVE("inactive"),
    BANNED("banned");

    private final String value;


    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static ProfileStatusEnum fromValue(String value) {
        if (value == null || value.isBlank()) return null;
        for (ProfileStatusEnum e : values()) {
            if (e.value.equalsIgnoreCase(value)) return e;
        }
        throw new IllegalArgumentException("Invalid value for ProfileStatusEnum: '" + value + "'");
    }
}
