package io.github.nguyennhatquang.fashion.common.Enum;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Represents the processing status of an outbox event.
 * PostgreSQL type: outbox_status_enum
 * JSON wire values: "pending" | "published" | "failed"
 */
public enum OutboxStatusEnum {

    PENDING("pending"),
    PUBLISHED("published"),
    FAILED("failed");

    private final String value;

    OutboxStatusEnum(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static OutboxStatusEnum fromValue(String value) {
        if (value == null || value.isBlank()) return null;
        for (OutboxStatusEnum e : values()) {
            if (e.value.equalsIgnoreCase(value)) return e;
        }
        throw new IllegalArgumentException("Invalid value for OutboxStatusEnum: '" + value + "'");
    }
}
