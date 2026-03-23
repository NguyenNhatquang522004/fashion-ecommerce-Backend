package io.github.nguyennhatquang.fashion.common.Enum;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Represents the lifecycle status of a customer order.
 * PostgreSQL type: order_status_enum
 * JSON wire values: "pending" | "reserved" | "paid" | "processing" | "shipping" | "completed" | "cancelled"
 *
 * Lifecycle (happy path): PENDING → RESERVED → PAID → PROCESSING → SHIPPING → COMPLETED
 * Cancellation: any state before SHIPPING → CANCELLED
 */
@Getter
@RequiredArgsConstructor
public enum OrderStatusEnum {

    PENDING("pending"),
    RESERVED("reserved"),
    PAID("paid"),
    PROCESSING("processing"),
    SHIPPING("shipping"),
    COMPLETED("completed"),
    CANCELLED("cancelled");

    private final String value;

 
    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static OrderStatusEnum fromValue(String value) {
        if (value == null || value.isBlank()) return null;
        for (OrderStatusEnum e : values()) {
            if (e.value.equalsIgnoreCase(value)) return e;
        }
        throw new IllegalArgumentException("Invalid value for OrderStatusEnum: '" + value + "'");
    }
}
