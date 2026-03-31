package io.github.nguyennhatquang.fashion.common.Enum;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReplyStatusEnum {
    SUCCESS, FAILED;

    @JsonValue
    public String getValue() {
        return name();
    }

    @JsonCreator
    public static ReplyStatusEnum fromValue(String value) {
        if (value == null || value.isBlank())
            return null;
        for (ReplyStatusEnum e : values()) {
            if (e.name().equalsIgnoreCase(value))
                return e;
        }
        throw new IllegalArgumentException("Invalid value for ReplyStatusEnum: '" + value + "'");
    }
}
