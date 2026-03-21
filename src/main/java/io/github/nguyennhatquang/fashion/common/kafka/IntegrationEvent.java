package io.github.nguyennhatquang.fashion.common.kafka;

import io.github.nguyennhatquang.fashion.common.Enum.EventType;

public record IntegrationEvent<T>(String eventId, EventType eventType, EventContext ctx, T payload, long timestamp) {
    public IntegrationEvent(EventType eventType, EventContext ctx, T payload) {
        this(java.util.UUID.randomUUID().toString(), eventType, ctx, payload, System.currentTimeMillis());
    }
}