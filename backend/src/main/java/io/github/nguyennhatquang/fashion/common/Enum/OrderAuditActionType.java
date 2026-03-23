package io.github.nguyennhatquang.fashion.common.Enum;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Represents the action type recorded in the order audit log (Cassandra).
 * JSON wire values: "create" | "update" | "cancel" | "refund"
 */
@Getter
@RequiredArgsConstructor
public enum OrderAuditActionType {

    CREATE("create"),
    UPDATE("update"),
    CANCEL("cancel"),
    REFUND("refund");

    private final String value;



    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static OrderAuditActionType fromValue(String value) {
        if (value == null || value.isBlank()) return null;
        for (OrderAuditActionType e : values()) {
            if (e.value.equalsIgnoreCase(value)) return e;
        }
        throw new IllegalArgumentException("Invalid value for OrderAuditActionType: '" + value + "'");
    }
}
