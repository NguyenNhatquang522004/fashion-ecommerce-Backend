package io.github.nguyennhatquang.fashion.common.Enum;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Represents the overall status of a distributed saga execution.
 * PostgreSQL type: saga_status_enum
 * JSON wire values: "started" | "completed" | "compensating" | "aborted"
 */
@Getter
@RequiredArgsConstructor
public enum SagaStatusEnum {

    STARTED("started"),
    COMPLETED("completed"),
    COMPENSATING("compensating"),
    ABORTED("aborted");

    private final String value;


    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static SagaStatusEnum fromValue(String value) {
        if (value == null || value.isBlank()) return null;
        for (SagaStatusEnum e : values()) {
            if (e.value.equalsIgnoreCase(value)) return e;
        }
        throw new IllegalArgumentException("Invalid value for SagaStatusEnum: '" + value + "'");
    }
}
