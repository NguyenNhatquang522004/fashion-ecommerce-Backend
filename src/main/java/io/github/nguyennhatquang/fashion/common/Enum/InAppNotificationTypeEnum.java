package io.github.nguyennhatquang.fashion.common.Enum;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Represents the category of an in-app notification.
 * MongoDB field type: String (stored as lowercase value)
 * JSON wire values: "order" | "promotion" | "system" | "ai_stylist"
 */
@Getter
@RequiredArgsConstructor
public enum InAppNotificationTypeEnum {

    ORDER("order"),
    PROMOTION("promotion"),
    SYSTEM("system"),
    AI_STYLIST("ai_stylist");

    private final String value;

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static InAppNotificationTypeEnum fromValue(String value) {
        if (value == null || value.isBlank()) return null;
        for (InAppNotificationTypeEnum e : values()) {
            if (e.value.equalsIgnoreCase(value)) return e;
        }
        throw new IllegalArgumentException("Invalid value for InAppNotificationTypeEnum: '" + value + "'");
    }
}
