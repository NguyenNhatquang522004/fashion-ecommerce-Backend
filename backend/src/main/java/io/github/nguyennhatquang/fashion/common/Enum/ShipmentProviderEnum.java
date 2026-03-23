package io.github.nguyennhatquang.fashion.common.Enum;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Represents the third-party logistics carrier used for shipment.
 * PostgreSQL type: shipment_provider_enum
 * JSON wire values: "ghn" | "ghtk" | "ninjavan"
 */
@Getter
@RequiredArgsConstructor
public enum ShipmentProviderEnum {

    GHN("ghn"),
    GHTK("ghtk"),
    NINJAVAN("ninjavan");

    private final String value;

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static ShipmentProviderEnum fromValue(String value) {
        if (value == null || value.isBlank()) return null;
        for (ShipmentProviderEnum e : values()) {
            if (e.value.equalsIgnoreCase(value)) return e;
        }
        throw new IllegalArgumentException("Invalid value for ShipmentProviderEnum: '" + value + "'");
    }
}
