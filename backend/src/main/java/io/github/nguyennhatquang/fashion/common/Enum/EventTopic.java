package io.github.nguyennhatquang.fashion.common.Enum;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import io.github.nguyennhatquang.fashion.Inventory.domain.entity.StockReservation;

/**
 * Represents Kafka topic names used for inter-service messaging (pre-existing
 * enum).
 * JSON wire values follow the kebab-case topic naming convention.
 *
 * Examples: "order-created", "payment-processed", "inventory-reserved", etc.
 */
public enum EventTopic {
    StockReservation_Clear("stock-reservation", 3, 1),
    BRAND_DELETE("brand-delete", 3, 1),
    ORDER_CREATED("order-created", 3, 1),
    PAYMENT_PROCESSED("payment-processed", 3, 1),
    PAYMENT_FAILED("payment-failed", 3, 1),
    INVENTORY_RESERVED("inventory-reserved", 3, 1),
    INVENTORY_RELEASED("inventory-released", 3, 1),
    ORDER_COMPLETED("order-completed", 3, 1),
    ORDER_CANCELLED("order-cancelled", 3, 1),
    NOTIFICATION_SEND("notification-send", 3, 1),
    LOYALTY_POINTS_EARNED("loyalty-points-earned", 3, 1);

    public static class TopicName {
        public static final String BRAND_DELETE = "brand-delete";
        public static final String ORDER_CREATED = "order-created";
        public static final String PAYMENT_PROCESSED = "payment-processed";
        public static final String STOCK_RESERVATION_CLEAR = "stock-reservation-clear";
        public static final String ORDER_CANCELLED = "order-cancelled";
        // ...
    }

    private final String value;
    private final int partitions;
    private final int replicas;

    EventTopic(String value, int partitions, int replicas) {
        this.value = value;
        this.partitions = partitions;
        this.replicas = replicas;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    public int getPartitions() {
        return partitions;
    }

    public int getReplicas() {
        return replicas;
    }

    @JsonCreator
    public static EventTopic fromValue(String value) {
        if (value == null || value.isBlank())
            return null;
        for (EventTopic e : values()) {
            if (e.value.equalsIgnoreCase(value))
                return e;
        }
        throw new IllegalArgumentException("Invalid value for EventTopic: '" + value + "'");
    }
}
