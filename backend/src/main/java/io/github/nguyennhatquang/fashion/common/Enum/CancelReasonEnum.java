package io.github.nguyennhatquang.fashion.common.Enum;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CancelReasonEnum {
    PAYMENT_TIMEOUT, // Khách treo đơn quá TTL
    PAYMENT_FAILED, // Thẻ lỗi, Momo lỗi
    USER_CANCELLED, // Khách tự bấm hủy
    FRAUD_DETECTED; // Hệ thống phát hiện gian lận

    @JsonValue
    public String getValue() {
        return name();
    }

    @JsonCreator
    public static CancelReasonEnum fromValue(String value) {
        if (value == null || value.isBlank())
            return null;
        for (CancelReasonEnum e : values()) {
            if (e.name().equalsIgnoreCase(value))
                return e;
        }
        throw new IllegalArgumentException("Invalid value for CancelReasonEnum: '" + value + "'");
    }
}
