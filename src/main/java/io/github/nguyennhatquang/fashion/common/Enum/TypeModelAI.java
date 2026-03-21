package io.github.nguyennhatquang.fashion.common.Enum;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Represents the AI language model used for styling recommendations (pre-existing enum).
 * JSON wire values: "gpt-4o" | "gpt-4o-mini" | "gemini-pro" | "claude-3-sonnet"
 *
 * Wire values follow provider API naming conventions.
 */
@Getter
@RequiredArgsConstructor
public enum TypeModelAI {

    GPT_4O("gpt-4o"),
    GPT_4O_MINI("gpt-4o-mini"),
    GEMINI_PRO("gemini-pro"),
    CLAUDE_3_SONNET("claude-3-sonnet");

    private final String value;



    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static TypeModelAI fromValue(String value) {
        if (value == null || value.isBlank()) return null;
        for (TypeModelAI e : values()) {
            if (e.value.equalsIgnoreCase(value)) return e;
        }
        throw new IllegalArgumentException("Invalid value for TypeModelAI: '" + value + "'");
    }
}
