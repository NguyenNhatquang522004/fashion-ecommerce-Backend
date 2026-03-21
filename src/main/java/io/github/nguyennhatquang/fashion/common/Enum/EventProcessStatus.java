package io.github.nguyennhatquang.fashion.common.Enum;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Represents the processing status of a Kafka event (pre-existing enum).
 * JSON wire values: "PENDING" | "PROCESSED" | "FAILED"
 *
 * Wire values are kept in UPPERCASE to preserve backward compatibility with
 * existing Kafka consumers that depend on this format.
 */
public enum EventProcessStatus {

    PENDING("PENDING"),
    PROCESSED("PROCESSED"),
    FAILED("FAILED");

    private final String value;

    EventProcessStatus(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static EventProcessStatus fromValue(String value) {
        if (value == null || value.isBlank()) return null;
        for (EventProcessStatus e : values()) {
            if (e.value.equalsIgnoreCase(value)) return e;
        }
        throw new IllegalArgumentException("Invalid value for EventProcessStatus: '" + value + "'");
    }
}
