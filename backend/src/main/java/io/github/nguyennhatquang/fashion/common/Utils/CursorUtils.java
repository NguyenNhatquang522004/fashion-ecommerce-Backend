package io.github.nguyennhatquang.fashion.common.Utils;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Base64;
import java.util.UUID;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import io.github.nguyennhatquang.fashion.common.request.CursorData;
import lombok.experimental.UtilityClass;

@UtilityClass
public class CursorUtils {
    private static final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

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

    public String encodeCursorV2(CursorData cursorData) {
        if (cursorData == null || cursorData.getSortValue() == null || cursorData.getId() == null) {
            return null;
        }
        try {
            // Chuyển Object thành chuỗi JSON
            String json = mapper.writeValueAsString(cursorData);
            // Mã hóa JSON thành Base64 an toàn cho URL
            return Base64.getUrlEncoder().withoutPadding().encodeToString(json.getBytes(StandardCharsets.UTF_8));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Lỗi khi encode cursor", e);
        }
    }

    public CursorData decodeCursorV2(String base64Cursor) {
        if (base64Cursor == null || base64Cursor.trim().isEmpty()) {
            return null;
        }
        try {
            // Giải mã Base64 về chuỗi JSON
            String json = new String(Base64.getUrlDecoder().decode(base64Cursor), StandardCharsets.UTF_8);
            // Chuyển JSON ngược lại thành Object CursorData
            return mapper.readValue(json, CursorData.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("Định dạng cursor không hợp lệ", e);
        }
    }
}
