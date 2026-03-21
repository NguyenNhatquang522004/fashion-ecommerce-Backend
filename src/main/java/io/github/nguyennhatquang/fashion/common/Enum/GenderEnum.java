package io.github.nguyennhatquang.fashion.common.Enum;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Represents the gender of a user profile.
 * PostgreSQL type: gender_enum
 * JSON wire values: "male" | "female" | "other"
 */
public enum GenderEnum {

    MALE("male"),
    FEMALE("female"),
    OTHER("other");

    private final String value;

    GenderEnum(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static GenderEnum fromValue(String value) {
        if (value == null || value.isBlank()) return null;
        for (GenderEnum e : values()) {
            if (e.value.equalsIgnoreCase(value)) return e;
        }
        throw new IllegalArgumentException("Invalid value for GenderEnum: '" + value + "'");
    }
}
