package io.github.nguyennhatquang.fashion.common.Enum;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Represents the conversational tone of the AI stylist persona.
 * PostgreSQL type: persona_tone_enum
 * JSON wire values: "friendly" | "professional" | "luxury"
 */
public enum PersonaToneEnum {

    FRIENDLY("friendly"),
    PROFESSIONAL("professional"),
    LUXURY("luxury");

    private final String value;

    PersonaToneEnum(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static PersonaToneEnum fromValue(String value) {
        if (value == null || value.isBlank()) return null;
        for (PersonaToneEnum e : values()) {
            if (e.value.equalsIgnoreCase(value)) return e;
        }
        throw new IllegalArgumentException("Invalid value for PersonaToneEnum: '" + value + "'");
    }
}
