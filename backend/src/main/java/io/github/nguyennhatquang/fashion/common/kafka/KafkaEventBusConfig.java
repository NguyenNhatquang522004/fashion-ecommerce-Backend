package io.github.nguyennhatquang.fashion.common.kafka;

import org.apache.kafka.common.TopicPartition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.ExponentialBackOffWithMaxRetries;
import org.springframework.messaging.converter.MessageConversionException;

// Import Custom Exception của dự án bạn
import io.github.nguyennhatquang.fashion.common.errors.UnprocessablePayloadException;

@Configuration
@EnableKafka
public class KafkaEventBusConfig {

    // 1. Cấu hình Error Handler & DLQ (Tấm lưới an toàn toàn cục)
    @Bean
    public DefaultErrorHandler errorHandler(KafkaTemplate<Object, Object> template) {

        // --- BƯỚC 1: ĐỊNH NGHĨA CÔNG CỤ ĐẨY VÀO DLQ ---
        // Tự động đẩy message lỗi vào topic .DLT (Dead Letter Topic) với cùng số
        // partition
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(template,
                (record, exception) -> new TopicPartition(record.topic() + ".DLT", record.partition()));

        // --- BƯỚC 2: CẤU HÌNH RETRY CHO LỖI B (Lỗi DB, Timeout...) ---
        // Retry 3 lần, delay tăng dần: 1s, 2s, 4s
        ExponentialBackOffWithMaxRetries backOff = new ExponentialBackOffWithMaxRetries(3);
        backOff.setInitialInterval(1000L);
        backOff.setMultiplier(2.0);

        // Nhúng Recoverer và BackOff vào ErrorHandler
        DefaultErrorHandler errorHandler = new DefaultErrorHandler(recoverer, backOff);

        // --- BƯỚC 3: XỬ LÝ LỖI A (ĐẨY THẲNG VÀO DLQ) ---
        // Khai báo các lỗi KHÔNG ĐƯỢC RETRY.
        // Khi văng ra các lỗi này, ErrorHandler sẽ lập tức dùng 'recoverer' ném vào
        // DLQ.
        errorHandler.addNotRetryableExceptions(
                IllegalArgumentException.class,
                MessageConversionException.class,
                UnprocessablePayloadException.class // Cấm Retry lỗi parse payload
        );

        // --- BƯỚC 4: LOG QUÁ TRÌNH RETRY ---
        // (Best Practice) Thêm log để theo dõi sát sao quá trình xử lý
        errorHandler.setRetryListeners((record, ex, deliveryAttempt) -> {
            org.slf4j.LoggerFactory.getLogger(KafkaEventBusConfig.class).warn(
                    "Đang Retry message [Offset: {}] lần thứ {} do lỗi: {}",
                    record.offset(), deliveryAttempt, ex.getMessage());
        });

        return errorHandler;
    }

    // 2. Factory cho Single Listener
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> singleKafkaListenerContainerFactory(
            ConsumerFactory<String, String> consumerFactory,
            DefaultErrorHandler errorHandler) {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.setCommonErrorHandler(errorHandler);
        // Bắt buộc: Tự commit offset khi hoàn thành hoặc khi đã tự xử lý xong lỗi
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        return factory;
    }

    // 3. Factory cho Batch Listener
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> batchKafkaListenerContainerFactory(
            ConsumerFactory<String, String> consumerFactory,
            DefaultErrorHandler errorHandler) {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.setCommonErrorHandler(errorHandler);
        factory.setBatchListener(true);
        // Bắt buộc: Tự commit offset khi hoàn thành hoặc khi đã tự xử lý xong lỗi
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        return factory;
    }
}