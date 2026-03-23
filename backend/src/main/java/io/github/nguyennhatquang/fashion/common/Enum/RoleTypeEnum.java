package io.github.nguyennhatquang.fashion.common.Enum;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RoleTypeEnum {
    ROLE_USER("User"),
    ROLE_ADMIN("Admin"),
    ROLE_STAFF("Staff"),
    ROLE_MANAGER("Manager"),
    ROLE_DELIVERY("Delivery"),
    ROLE_SUPPLIER("Supplier"),
    ROLE_CUSTOMER("Customer");

    private final String value;

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static RoleTypeEnum fromValue(String value) {
        if (value == null || value.isBlank())
            return null;
        for (RoleTypeEnum e : values()) {
            if (e.value.equalsIgnoreCase(value))
                return e;
        }
        throw new IllegalArgumentException("Invalid value for RoleTypeEnum: '" + value + "'");
    }
}
