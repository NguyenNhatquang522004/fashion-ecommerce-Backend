package io.github.nguyennhatquang.fashion.Order.delivery.Dto.OrderItem;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@UtilityClass
public class OrderItemRequest {
    public record OrderItemCreateRequest(
        @NotBlank(message = "Product ID không được để trống")
        String productId,

        @NotNull(message = "Order ID không được để trống")
        UUID orderId,

        @NotBlank(message = "Mã SKU không được để trống")
        String skuCode,

        @NotBlank(message = "Tên sản phẩm không được để trống")
        String productName,

        Map<String, Object> variantAttributes,

        @NotNull(message = "Đơn giá không được để trống")
        BigDecimal unitPrice,

        @NotNull(message = "Số lượng không được để trống")
        Integer quantity
    ) {}

    public record OrderItemUpdateRequest(
        @NotBlank(message = "Product ID không được để trống")
        String productId,

        @NotNull(message = "Order ID không được để trống")
        UUID orderId,

        @NotBlank(message = "Mã SKU không được để trống")
        String skuCode,

        @NotBlank(message = "Tên sản phẩm không được để trống")
        String productName,

        Map<String, Object> variantAttributes,

        @NotNull(message = "Đơn giá không được để trống")
        BigDecimal unitPrice,

        @NotNull(message = "Số lượng không được để trống")
        Integer quantity
    ) {}
}
