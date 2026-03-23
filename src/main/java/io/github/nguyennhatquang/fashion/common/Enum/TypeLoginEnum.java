package io.github.nguyennhatquang.fashion.common.Enum;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TypeLoginEnum {
    Local("local"),
    Google("google"),
    Facebook("facebook");

    private final String value;

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static TypeLoginEnum fromValue(String value) {
        if (value == null || value.isBlank())
            return null;
        for (TypeLoginEnum e : values()) {
            if (e.value.equalsIgnoreCase(value))
                return e;
        }
        throw new IllegalArgumentException("Invalid value for SystemErrorSeverity: '" + value + "'");
    }
}
