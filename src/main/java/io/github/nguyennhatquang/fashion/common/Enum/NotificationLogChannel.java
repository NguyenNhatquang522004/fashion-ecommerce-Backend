package io.github.nguyennhatquang.fashion.common.Enum;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Represents the delivery channel for a notification log entry (Cassandra).
 * JSON wire values: "email" | "sms" | "push" | "in_app"
 */
@Getter
@RequiredArgsConstructor
public enum NotificationLogChannel {

    EMAIL("email"),
    SMS("sms"),
    PUSH("push"),
    IN_APP("in_app");

    private final String value;


    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static NotificationLogChannel fromValue(String value) {
        if (value == null || value.isBlank()) return null;
        for (NotificationLogChannel e : values()) {
            if (e.value.equalsIgnoreCase(value)) return e;
        }
        throw new IllegalArgumentException("Invalid value for NotificationLogChannel: '" + value + "'");
    }
}
