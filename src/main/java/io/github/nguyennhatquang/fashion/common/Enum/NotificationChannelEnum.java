package io.github.nguyennhatquang.fashion.common.Enum;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Represents the channel through which a notification is delivered.
 * PostgreSQL type: notification_channel_enum
 * JSON wire values: "email" | "sms" | "push" | "in_app"
 */
public enum NotificationChannelEnum {

    EMAIL("email"),
    SMS("sms"),
    PUSH("push"),
    IN_APP("in_app");

    private final String value;

    NotificationChannelEnum(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static NotificationChannelEnum fromValue(String value) {
        if (value == null || value.isBlank()) return null;
        for (NotificationChannelEnum e : values()) {
            if (e.value.equalsIgnoreCase(value)) return e;
        }
        throw new IllegalArgumentException("Invalid value for NotificationChannelEnum: '" + value + "'");
    }
}
