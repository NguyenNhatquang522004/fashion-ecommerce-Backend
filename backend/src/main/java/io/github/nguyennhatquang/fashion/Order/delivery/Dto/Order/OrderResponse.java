package io.github.nguyennhatquang.fashion.Order.delivery.Dto.Order;

import lombok.Builder;
import lombok.Value;

import io.github.nguyennhatquang.fashion.common.Enum.OrderStatusEnum;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Value
@Builder
public class OrderResponse {
    UUID id;
    UUID userId;
    String orderCode;
    BigDecimal subtotalAmount;
    BigDecimal shippingFee;
    BigDecimal discountAmount;
    BigDecimal finalAmount;
    OrderStatusEnum status;
    Map<String, Object> shippingInfo;
    String note;
    Long version;
    String createdBy;
    LocalDateTime createdAt;
    String updatedBy;
    LocalDateTime updatedAt;
}
