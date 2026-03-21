package io.github.nguyennhatquang.fashion.common.Enum;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Represents the type of device for push notification targeting.
 * PostgreSQL type: device_type_enum
 * JSON wire values: "ios" | "android" | "web"
 */
public enum DeviceTypeEnum {

    IOS("ios"),
    ANDROID("android"),
    WEB("web");

    private final String value;

    DeviceTypeEnum(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static DeviceTypeEnum fromValue(String value) {
        if (value == null || value.isBlank()) return null;
        for (DeviceTypeEnum e : values()) {
            if (e.value.equalsIgnoreCase(value)) return e;
        }
        throw new IllegalArgumentException("Invalid value for DeviceTypeEnum: '" + value + "'");
    }
}
