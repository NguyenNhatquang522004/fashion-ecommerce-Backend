package io.github.nguyennhatquang.fashion.common.Utils;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Base64;
import java.util.UUID;

public class CursorUtils {
    // Tạo cursor từ LocalDateTime và UUID
    public static String encodeCursor(LocalDateTime createdAt, UUID id) {
        if (createdAt == null || id == null)
            return null;
        long timeMillis = createdAt.toInstant(ZoneOffset.UTC).toEpochMilli();
        String rawCursor = timeMillis + "|" + id.toString();
        return Base64.getUrlEncoder().withoutPadding().encodeToString(rawCursor.getBytes(StandardCharsets.UTF_8));
    }

    // Giải mã cursor về mảng [LocalDateTime, UUID]
    public static Object[] decodeCursor(String base64Cursor) {
        if (base64Cursor == null || base64Cursor.trim().isEmpty()) {
            return null;
        }
        try {
            String rawCursor = new String(Base64.getUrlDecoder().decode(base64Cursor), StandardCharsets.UTF_8);
            String[] parts = rawCursor.split("\\|");
            if (parts.length != 2)
                return null;

            LocalDateTime createdAt = Instant.ofEpochMilli(Long.parseLong(parts[0]))
                    .atZone(ZoneOffset.UTC).toLocalDateTime();
            UUID id = UUID.fromString(parts[1]);
            return new Object[] { createdAt, id };
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid cursor format");
        }
    }
}
