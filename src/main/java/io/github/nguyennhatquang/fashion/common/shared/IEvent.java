package io.github.nguyennhatquang.fashion.common.shared;



import io.github.nguyennhatquang.fashion.common.Enum.EventType;
import io.github.nguyennhatquang.fashion.common.kafka.EventContext;
import io.github.nguyennhatquang.fashion.common.kafka.IntegrationEvent;

public interface IEvent<T> {

    void publish(EventContext ctx, EventType eventType, String key, T payload);

    void Subscribe(EventContext ctx, IntegrationEvent<T> event);

    void SubscribeBatch(EventContext ctx, java.util.List<IntegrationEvent<T>> events);

}
