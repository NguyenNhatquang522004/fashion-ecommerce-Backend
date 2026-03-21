package io.github.nguyennhatquang.fashion.common.kafka;

import io.github.nguyennhatquang.fashion.common.Enum.EventTopic;
import io.github.nguyennhatquang.fashion.common.Enum.EventType;

public record IntegrationEvent<T>(String eventId, EventTopic eventTopic, EventType eventType, EventContext ctx,
        T payload, long timestamp) {
    public IntegrationEvent(EventTopic eventTopic, EventType eventType, EventContext ctx, T payload) {
        this(java.util.UUID.randomUUID().toString(), eventTopic, eventType, ctx, payload, System.currentTimeMillis());
    }
}