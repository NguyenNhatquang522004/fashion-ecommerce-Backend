package io.github.nguyennhatquang.fashion.common.kafka;

import java.nio.charset.StandardCharsets;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.MDC;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.nguyennhatquang.fashion.common.Enum.EventTopic;
import io.github.nguyennhatquang.fashion.common.Enum.EventType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Component chịu trách nhiệm đẩy mọi loại Event lên Kafka.
 * Tuân thủ 100% Best Practice: Tự động bọc Header, bọc MDC Log, bắt lỗi JSON.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EventPublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    /**
     * Hàm Publish Generic dùng cho MỌI trường hợp
     * * @param <T> Kiểu dữ liệu của Payload (Tự động nhận diện)
     * 
     * @param topic        Topic đích (Enum)
     * @param eventType    Loại hành động (Enum)
     * @param ctx          Ngữ cảnh truy vết (EventContext)
     * @param partitionKey Khóa định tuyến Kafka (Ví dụ: BrandID, OrderID...)
     * @param payload      Dữ liệu thực tế cần gửi
     */
    public <T> void publish(EventTopic topic, EventType eventType, EventContext ctx, String partitionKey, T payload) {

        // 1. Tự động bọc Payload vào vỏ IntegrationEvent
        IntegrationEvent<T> event = new IntegrationEvent<>(topic, eventType, ctx, payload);

        try {
            // 2. Chuyển đổi sang JSON
            String jsonPayload = objectMapper.writeValueAsString(event);

            // 3. Tạo Record với Khóa định tuyến (Bảo vệ tính thứ tự)
            ProducerRecord<String, String> record = new ProducerRecord<>(
                    topic.getValue(),
                    partitionKey,
                    jsonPayload);

            // 4. Bơm Headers tự động cho toàn bộ hệ thống
            record.headers().add("X-Event-ID", event.eventId().getBytes(StandardCharsets.UTF_8));
            record.headers().add("X-Correlation-ID", ctx.correlationId().getBytes(StandardCharsets.UTF_8));
            record.headers().add("X-Event-Type", eventType.name().getBytes(StandardCharsets.UTF_8));

            final String traceId = ctx.correlationId();

            // 5. Bắn Async và khôi phục ngữ cảnh Log
            kafkaTemplate.send(record).whenComplete((result, ex) -> {
                try (MDC.MDCCloseable ignored = MDC.putCloseable("correlationId", traceId)) {
                    if (ex == null) {
                        log.info("Đã gửi Kafka Event [{}] - Topic: {} - Partition: {}",
                                event.eventId(), topic.getValue(), result.getRecordMetadata().partition());
                    } else {
                        log.error("LỖI MẠNG: Không thể gửi Kafka Event [{}]. Lý do: {}",
                                event.eventId(), ex.getMessage());
                    }
                }
            });

        } catch (JsonProcessingException e) {
            log.error("[{}] Lỗi Serialize JSON Payload: {}", ctx.correlationId(), payload.getClass().getSimpleName(),
                    e);
            throw new RuntimeException("Lỗi hệ thống khi tạo Message Kafka", e);
        }
    }
}