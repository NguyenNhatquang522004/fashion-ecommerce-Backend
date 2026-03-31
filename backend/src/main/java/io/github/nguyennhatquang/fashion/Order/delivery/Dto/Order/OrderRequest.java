package io.github.nguyennhatquang.fashion.Order.delivery.Dto.Order;

import jakarta.validation.constraints.NotNull;
import lombok.experimental.UtilityClass;

import io.github.nguyennhatquang.fashion.common.Enum.OrderStatusEnum;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@UtilityClass
public class OrderRequest {
    public record OrderCreateRequest(
        @NotNull(message = "User ID không được để trống")
        UUID userId,

        @NotNull(message = "Mã đơn hàng không được để trống")
        String orderCode,

        @NotNull(message = "Tổng phụ không được để trống")
        BigDecimal subtotalAmount,

        @NotNull(message = "Phí giao hàng không được để trống")
        BigDecimal shippingFee,

        @NotNull(message = "Số tiền giảm giá không được để trống")
        BigDecimal discountAmount,

        @NotNull(message = "Tổng tiền thanh toán không được để trống")
        BigDecimal finalAmount,

        @NotNull(message = "Trạng thái đơn hàng không được để trống")
        OrderStatusEnum status,

        @NotNull(message = "Thông tin giao hàng không được để trống")
        Map<String, Object> shippingInfo,

        String note,

        Long version
    ) {}

    public record OrderUpdateRequest(
        @NotNull(message = "User ID không được để trống")
        UUID userId,

        @NotNull(message = "Mã đơn hàng không được để trống")
        String orderCode,

        @NotNull(message = "Tổng phụ không được để trống")
        BigDecimal subtotalAmount,

        @NotNull(message = "Phí giao hàng không được để trống")
        BigDecimal shippingFee,

        @NotNull(message = "Số tiền giảm giá không được để trống")
        BigDecimal discountAmount,

        @NotNull(message = "Tổng tiền thanh toán không được để trống")
        BigDecimal finalAmount,

        @NotNull(message = "Trạng thái đơn hàng không được để trống")
        OrderStatusEnum status,

        @NotNull(message = "Thông tin giao hàng không được để trống")
        Map<String, Object> shippingInfo,

        String note,

        Long version
    ) {}
}
