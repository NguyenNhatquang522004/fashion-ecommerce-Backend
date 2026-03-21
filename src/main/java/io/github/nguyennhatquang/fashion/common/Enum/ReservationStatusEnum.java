package io.github.nguyennhatquang.fashion.common.Enum;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Represents the lifecycle status of an inventory reservation.
 * PostgreSQL type: reservation_status_enum
 * JSON wire values: "reserved" | "confirmed" | "cancelled" | "expired"
 */
@Getter
@RequiredArgsConstructor
public enum ReservationStatusEnum {

    RESERVED("reserved"),
    CONFIRMED("confirmed"),
    CANCELLED("cancelled"),
    EXPIRED("expired");

    private final String value;


    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static ReservationStatusEnum fromValue(String value) {
        if (value == null || value.isBlank()) return null;
        for (ReservationStatusEnum e : values()) {
            if (e.value.equalsIgnoreCase(value)) return e;
        }
        throw new IllegalArgumentException("Invalid value for ReservationStatusEnum: '" + value + "'");
    }
}
