package io.github.nguyennhatquang.fashion.common.Enum;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Represents the severity level of a system error log entry (Cassandra).
 * JSON wire values: "info" | "warning" | "error" | "critical"
 */
public enum SystemErrorSeverity {

    INFO("info"),
    WARNING("warning"),
    ERROR("error"),
    CRITICAL("critical");

    private final String value;

    SystemErrorSeverity(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static SystemErrorSeverity fromValue(String value) {
        if (value == null || value.isBlank()) return null;
        for (SystemErrorSeverity e : values()) {
            if (e.value.equalsIgnoreCase(value)) return e;
        }
        throw new IllegalArgumentException("Invalid value for SystemErrorSeverity: '" + value + "'");
    }
}
