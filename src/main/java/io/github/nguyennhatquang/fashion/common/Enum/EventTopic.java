package io.github.nguyennhatquang.fashion.common.Enum;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Represents Kafka topic names used for inter-service messaging (pre-existing enum).
 * JSON wire values follow the kebab-case topic naming convention.
 *
 * Examples: "order-created", "payment-processed", "inventory-reserved", etc.
 */
public enum EventTopic {

    ORDER_CREATED("order-created"),
    PAYMENT_PROCESSED("payment-processed"),
    PAYMENT_FAILED("payment-failed"),
    INVENTORY_RESERVED("inventory-reserved"),
    INVENTORY_RELEASED("inventory-released"),
    ORDER_COMPLETED("order-completed"),
    ORDER_CANCELLED("order-cancelled"),
    NOTIFICATION_SEND("notification-send"),
    LOYALTY_POINTS_EARNED("loyalty-points-earned");

    private final String value;

    EventTopic(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static EventTopic fromValue(String value) {
        if (value == null || value.isBlank()) return null;
        for (EventTopic e : values()) {
            if (e.value.equalsIgnoreCase(value)) return e;
        }
        throw new IllegalArgumentException("Invalid value for EventTopic: '" + value + "'");
    }
}
