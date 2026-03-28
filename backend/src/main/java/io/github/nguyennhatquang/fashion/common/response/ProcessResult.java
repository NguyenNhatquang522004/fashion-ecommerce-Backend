package io.github.nguyennhatquang.fashion.common.response;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import com.google.auto.value.AutoValue.Builder;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcessResult {
    private boolean success;
    private ConsumerRecord<String, String> record;
    private Exception exception;

    public static ProcessResult success(ConsumerRecord<String, String> record) {
        return new ProcessResult(true, record, null);
    }

    public static ProcessResult fail(ConsumerRecord<String, String> record, Exception e) {
        return new ProcessResult(false, record, e);
    }
}
