package io.github.nguyennhatquang.fashion.common.Enum;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Represents the media type of a product asset.
 * MongoDB field type: String (stored as lowercase value)
 * JSON wire values: "image" | "video"
 *
 * Note: constants renamed from lowercase (image/video) to UPPERCASE (IMAGE/VIDEO)
 * to comply with Java naming conventions. Wire values are unchanged.
 */
public enum ProductMediaTypeEnum {

    IMAGE("image"),
    VIDEO("video");

    private final String value;

    ProductMediaTypeEnum(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static ProductMediaTypeEnum fromValue(String value) {
        if (value == null || value.isBlank()) return null;
        for (ProductMediaTypeEnum e : values()) {
            if (e.value.equalsIgnoreCase(value)) return e;
        }
        throw new IllegalArgumentException("Invalid value for ProductMediaTypeEnum: '" + value + "'");
    }
}
