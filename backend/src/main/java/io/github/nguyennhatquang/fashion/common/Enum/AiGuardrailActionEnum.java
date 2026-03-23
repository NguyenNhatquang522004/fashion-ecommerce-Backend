package io.github.nguyennhatquang.fashion.common.Enum;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Represents the action taken by the AI guardrail system.
 * PostgreSQL type: ai_guardrail_action_enum
 * JSON wire values: "block" | "redirect"
 */
@Getter
@RequiredArgsConstructor
public enum AiGuardrailActionEnum {

    BLOCK("block"),
    REDIRECT("redirect"); 

    private final String value;



    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static AiGuardrailActionEnum fromValue(String value) {
        if (value == null || value.isBlank()) return null;
        for (AiGuardrailActionEnum e : values()) {
            if (e.value.equalsIgnoreCase(value)) return e;
        }
        throw new IllegalArgumentException("Invalid value for AiGuardrailActionEnum: '" + value + "'");
    }
}
