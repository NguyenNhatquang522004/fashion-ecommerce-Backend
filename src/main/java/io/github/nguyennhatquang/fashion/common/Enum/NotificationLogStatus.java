package io.github.nguyennhatquang.fashion.common.Enum;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Represents the delivery status of a notification log entry (Cassandra).
 * JSON wire values: "sent" | "delivered" | "failed" | "read"
 */
public enum NotificationLogStatus {

    SENT("sent"),
    DELIVERED("delivered"),
    FAILED("failed"),
    READ("read");

    private final String value;

    NotificationLogStatus(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static NotificationLogStatus fromValue(String value) {
        if (value == null || value.isBlank()) return null;
        for (NotificationLogStatus e : values()) {
            if (e.value.equalsIgnoreCase(value)) return e;
        }
        throw new IllegalArgumentException("Invalid value for NotificationLogStatus: '" + value + "'");
    }
}
