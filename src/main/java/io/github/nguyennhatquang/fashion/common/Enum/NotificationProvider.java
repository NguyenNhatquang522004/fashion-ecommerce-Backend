package io.github.nguyennhatquang.fashion.common.Enum;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Represents the third-party provider used to dispatch a notification (Cassandra).
 * JSON wire values: "sendgrid" | "twilio" | "firebase" | "apns"
 */
public enum NotificationProvider {

    SENDGRID("sendgrid"),
    TWILIO("twilio"),
    FIREBASE("firebase"),
    APNS("apns");

    private final String value;

    NotificationProvider(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static NotificationProvider fromValue(String value) {
        if (value == null || value.isBlank()) return null;
        for (NotificationProvider e : values()) {
            if (e.value.equalsIgnoreCase(value)) return e;
        }
        throw new IllegalArgumentException("Invalid value for NotificationProvider: '" + value + "'");
    }
}
