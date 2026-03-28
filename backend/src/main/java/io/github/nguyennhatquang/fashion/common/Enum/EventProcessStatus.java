package io.github.nguyennhatquang.fashion.common.Enum;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Represents the processing status of a Kafka event (pre-existing enum).
 * JSON wire values: "PENDING" | "PROCESSED" | "FAILED"
 *
 * Wire values are kept in UPPERCASE to preserve backward compatibility with
 * existing Kafka consumers that depend on this format.
 */
public enum EventProcessStatus {
    SUCCESS, // Đã xử lý thành công -> Bỏ qua
    INVALID_PAYLOAD, // (Cũ: ERROR_A) Lỗi cấu trúc JSON, thiếu Data -> Nằm trong DLQ -> Bỏ qua
    TRANSIENT_ERROR; // (Cũ: ERROR_B) Lỗi mạng, DB, Timeout tạm thời -> Cho phép Kafka Retry

    /**
     * Kiểm tra trạng thái chốt sổ (Không bao giờ xử lý lại nữa)
     */
    public static boolean isFinalStatus(String statusStr) {
        if (statusStr == null || statusStr.isBlank()) {
            return false;
        }
        return SUCCESS.name().equals(statusStr) || INVALID_PAYLOAD.name().equals(statusStr);
    }
}
