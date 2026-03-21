package io.github.nguyennhatquang.fashion.common.Enum;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Represents the tracking status of a shipment.
 * PostgreSQL type: shipment_status_enum
 * JSON wire values: "preparing" | "picked_up" | "in_transit" | "delivered" | "returned"
 */
public enum ShipmentStatusEnum {

    PREPARING("preparing"),
    PICKED_UP("picked_up"),
    IN_TRANSIT("in_transit"),
    DELIVERED("delivered"),
    RETURNED("returned");

    private final String value;

    ShipmentStatusEnum(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static ShipmentStatusEnum fromValue(String value) {
        if (value == null || value.isBlank()) return null;
        for (ShipmentStatusEnum e : values()) {
            if (e.value.equalsIgnoreCase(value)) return e;
        }
        throw new IllegalArgumentException("Invalid value for ShipmentStatusEnum: '" + value + "'");
    }
}
