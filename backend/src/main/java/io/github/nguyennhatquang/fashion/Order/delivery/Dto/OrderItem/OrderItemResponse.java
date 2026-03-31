package io.github.nguyennhatquang.fashion.Order.delivery.Dto.OrderItem;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Value
@Builder
public class OrderItemResponse {
    UUID id;
    String productId;
    UUID orderId;
    String skuCode;
    String productName;
    Map<String, Object> variantAttributes;
    BigDecimal unitPrice;
    Integer quantity;
    BigDecimal subtotal;
    String createdBy;
    LocalDateTime createdAt;
    String updatedBy;
    LocalDateTime updatedAt;
}
