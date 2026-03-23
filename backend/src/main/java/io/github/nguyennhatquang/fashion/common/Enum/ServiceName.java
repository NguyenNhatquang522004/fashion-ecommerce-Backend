package io.github.nguyennhatquang.fashion.common.Enum;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Represents a microservice name within the platform (Cassandra).
 * JSON wire values: "order-service" | "inventory-service" | "payment-service"
 *                 | "notification-service" | "api-gateway"
 */
@Getter
@RequiredArgsConstructor
public enum ServiceName {

    ORDER_SERVICE("order-service"),
    INVENTORY_SERVICE("inventory-service"),
    PAYMENT_SERVICE("payment-service"),
    NOTIFICATION_SERVICE("notification-service"),
    API_GATEWAY("api-gateway");

    private final String value;


    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static ServiceName fromValue(String value) {
        if (value == null || value.isBlank()) return null;
        for (ServiceName e : values()) {
            if (e.value.equalsIgnoreCase(value)) return e;
        }
        throw new IllegalArgumentException("Invalid value for ServiceName: '" + value + "'");
    }
}
