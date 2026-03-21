package io.github.nguyennhatquang.fashion.common.Enum;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Represents the publication status of a product in the catalog.
 * MongoDB field type: String (stored as lowercase value)
 * JSON wire values: "draft" | "active" | "archived"
 */
public enum ProductStatusEnum {

    DRAFT("draft"),
    ACTIVE("active"),
    ARCHIVED("archived");

    private final String value;

    ProductStatusEnum(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static ProductStatusEnum fromValue(String value) {
        if (value == null || value.isBlank()) return null;
        for (ProductStatusEnum e : values()) {
            if (e.value.equalsIgnoreCase(value)) return e;
        }
        throw new IllegalArgumentException("Invalid value for ProductStatusEnum: '" + value + "'");
    }
}
