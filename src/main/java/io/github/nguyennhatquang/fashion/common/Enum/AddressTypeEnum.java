package io.github.nguyennhatquang.fashion.common.Enum;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Represents the type of a user address.
 * PostgreSQL type: address_type_enum
 * JSON wire values: "home" | "work" | "other"
 */
public enum AddressTypeEnum {

    HOME("home"),
    WORK("work"),
    OTHER("other");

    private final String value;

    AddressTypeEnum(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static AddressTypeEnum fromValue(String value) {
        if (value == null || value.isBlank())
            return null;
        for (AddressTypeEnum e : values()) {
            if (e.value.equalsIgnoreCase(value))
                return e;
        }
        throw new IllegalArgumentException("Invalid value for AddressTypeEnum: '" + value + "'");
    }}
            
                