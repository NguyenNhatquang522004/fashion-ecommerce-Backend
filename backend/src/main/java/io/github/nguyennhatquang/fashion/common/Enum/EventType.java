package io.github.nguyennhatquang.fashion.common.Enum;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Represents the category of a domain event (pre-existing enum).
 * JSON wire values: "ORDER" | "PAYMENT" | "INVENTORY" | "NOTIFICATION" |
 * "LOYALTY"
 *
 * Wire values are kept in UPPERCASE to preserve backward compatibility with
 * existing Kafka consumers and outbox event routing.
 */
public enum EventType {

    CREATED("CREATED"),
    UPDATED("UPDATED"),
    DELETED("DELETED");

    private final String value;

    EventType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static EventType fromValue(String value) {
        if (value == null || value.isBlank())
            return null;
        for (EventType e : values()) {
            if (e.value.equalsIgnoreCase(value))
                return e;
        }
        throw new IllegalArgumentException("Invalid value for EventType: '" + value + "'");
    }
}
