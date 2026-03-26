package io.github.nguyennhatquang.fashion.common.Utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ConvertUtils {
    // Múi giờ mặc định của hệ thống hoặc dự án (Ví dụ: Việt Nam)
    public static final ZoneId DEFAULT_ZONE = ZoneId.of("Asia/Ho_Chi_Minh");

    /**
     * Chuyển từ LocalDateTime sang Instant dựa trên múi giờ hệ thống hiện tại.
     * Thường dùng khi bạn lấy LocalDateTime.now() từ máy chủ.
     */
    public static Instant toInstant(LocalDateTime localDateTime) {
        if (localDateTime == null)
            return null;
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant();
    }

    /**
     * Chuyển từ LocalDateTime sang Instant với một Múi giờ cụ thể.
     * Cực kỳ hữu ích khi bạn biết chắc chắn dữ liệu đó thuộc múi giờ nào.
     */
    public static Instant toInstant(LocalDateTime localDateTime, ZoneId zoneId) {
        if (localDateTime == null)
            return null;
        return localDateTime.atZone(zoneId).toInstant();
    }

    /**
     * Chuyển từ LocalDateTime sang Instant coi như nó là UTC luôn (không cộng trừ
     * múi giờ).
     */
    public static Instant toUTCInstant(LocalDateTime localDateTime) {
        if (localDateTime == null)
            return null;
        return localDateTime.toInstant(ZoneOffset.UTC);
    }

    public LocalDateTime toLocalDateTime(Instant instant) {
        if (instant == null)
            return null;
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }

    /**
     * Chuyển từ Instant sang LocalDateTime với một Múi giờ cụ thể (Ví dụ: Việt
     * Nam).
     */
    public LocalDateTime toLocalDateTime(Instant instant, ZoneId zoneId) {
        if (instant == null)
            return null;
        return LocalDateTime.ofInstant(instant, zoneId);
    }

    /**
     * Chuyển từ Instant sang LocalDateTime theo chuẩn UTC (00:00).
     */
    public LocalDateTime toUTCLocalDateTime(Instant instant) {
        if (instant == null)
            return null;
        return LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
    }

    public UUID toUUID(String uuidString) {
        if (uuidString == null || uuidString.isBlank()) {
            return null;
        }
        try {
            return UUID.fromString(uuidString.trim());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
    public String toString(UUID uuid) {
        return Optional.ofNullable(uuid)
                .map(UUID::toString)
                .orElse(null);
    }

    public boolean isValidUUID(String uuidString) {
        if (uuidString == null || uuidString.isBlank()) return false;
        try {
            UUID.fromString(uuidString);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
