package io.github.nguyennhatquang.fashion.common.Enum;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Represents the delivery status of a notification log entry (Cassandra).
 * JSON wire values: "sent" | "delivered" | "failed" | "read"
 */
@Getter
@RequiredArgsConstructor
public enum NotificationLogStatus {

    SENT("sent"),
    DELIVERED("delivered"),
    FAILED("failed"),
    READ("read");

    private final String value;



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
