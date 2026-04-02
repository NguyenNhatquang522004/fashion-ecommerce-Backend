package io.github.nguyennhatquang.fashion.common.Utils;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import io.github.nguyennhatquang.fashion.Order.delivery.Dto.OrderItem.OrderItemRequest.VariantAttributesDto;
import io.github.nguyennhatquang.fashion.common.errors.UnprocessablePayloadException;
import io.github.nguyennhatquang.fashion.common.kafka.IntegrationEvent;
import lombok.experimental.UtilityClass;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

@UtilityClass
public class ParseUtils {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public <T> IntegrationEvent<T> parseEventSafe(String json, Class<T> payloadClass) {
        try {

            JavaType type = objectMapper.getTypeFactory()
                    .constructParametricType(IntegrationEvent.class, payloadClass);

            // Đọc JSON dựa trên cái khuôn (type) vừa nặn ra
            return objectMapper.readValue(json, type);

        } catch (Exception e) {
            // Báo lỗi rõ ràng class nào đang bị parse xịt để Dev dễ debug
            throw new UnprocessablePayloadException(
                    "Sai định dạng JSON. Không thể ép kiểu sang Payload: " + payloadClass.getSimpleName(), e);
        }
    }

    public Optional<String> getHeaderValue(org.apache.kafka.common.header.Headers headers, String key) {
        // 1. Kiểm tra lớp ngoài cùng
        if (headers == null || key == null || key.isBlank()) {
            return Optional.empty();
        }

        // 2. Lấy header cuối cùng (Kafka cho phép nhiều header trùng tên, ta lấy cái
        // mới nhất)
        var header = headers.lastHeader(key);

        // 3. Kiểm tra xem header đó có tồn tại và value của nó có dữ liệu không
        if (header == null || header.value() == null) {
            return Optional.empty();
        }

        // 4. Decode an toàn với UTF-8
        return Optional.of(new String(header.value(), StandardCharsets.UTF_8));
    }

    public Map<String, Object> toVariantAttributesMap(VariantAttributesDto dto) {
        if (dto == null) {
            return new HashMap<>();
        }

        Map<String, Object> map = new HashMap<>();

        // 3.1 Gắn Options
        map.put("options", dto.options() != null ? dto.options() : new HashMap<>());

        // 3.2 Gắn Presentation (Ánh xạ chuẩn Snake_case)
        if (dto.presentation() != null) {
            Map<String, Object> presentationMap = new HashMap<>();
            presentationMap.put("brand_name", dto.presentation().brandName());
            presentationMap.put("variant_image_url", dto.presentation().variantImageUrl());
            map.put("presentation", presentationMap);
        }

        // 3.3 Gắn Fulfillment (Ánh xạ chuẩn Snake_case)
        if (dto.fulfillment() != null) {
            Map<String, Object> fulfillmentMap = new HashMap<>();
            fulfillmentMap.put("weight_grams", dto.fulfillment().weightGrams());
            fulfillmentMap.put("barcode", dto.fulfillment().barcode());
            map.put("fulfillment", fulfillmentMap);
        }

        return map;
    }
}
