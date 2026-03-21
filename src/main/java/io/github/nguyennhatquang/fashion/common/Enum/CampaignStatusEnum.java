package io.github.nguyennhatquang.fashion.common.Enum;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Represents the lifecycle status of a marketing campaign.
 * PostgreSQL type: campaign_status_enum
 * JSON wire values: "draft" | "published" | "active" | "ended"
 */
@Getter
@RequiredArgsConstructor
public enum CampaignStatusEnum {

    DRAFT("draft"),
    PUBLISHED("published"),
    ACTIVE("active"),
    ENDED("ended");

    private final String value;

    CampaignStatusEnum(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static CampaignStatusEnum fromValue(String value) {
        if (value == null || value.isBlank())
            return null;
        for (CampaignStatusEnum e : values()) {
            if (e.value.equalsIgnoreCase(value))
                return e;
        }
        throw new IllegalArgumentException("Invalid value for CampaignStatusEnum: '" + value + "'");
    }
}
