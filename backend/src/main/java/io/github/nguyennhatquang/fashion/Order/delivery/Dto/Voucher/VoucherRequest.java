package io.github.nguyennhatquang.fashion.Order.delivery.Dto.Voucher;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.experimental.UtilityClass;

import io.github.nguyennhatquang.fashion.common.Enum.DiscountTypeEnum;
import io.github.nguyennhatquang.fashion.common.Enum.VoucherStatusEnum;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@UtilityClass
public class VoucherRequest {
    public record VoucherCreateRequest(
        @NotBlank(message = "Mã voucher không được để trống")
        String code,

        @NotNull(message = "Loại giảm giá không được để trống")
        DiscountTypeEnum discountType,

        @NotNull(message = "Giá trị giảm giá không được để trống")
        BigDecimal discountValue,

        BigDecimal minOrderValue,
        
        BigDecimal maxDiscountAmount,

        @NotNull(message = "Tổng số lượng không được để trống")
        Integer totalQuantity,

        @NotNull(message = "Số lượng đã sử dụng không được để trống")
        Integer usedQuantity,

        @NotNull(message = "Thời gian bắt đầu không được để trống")
        LocalDateTime startTime,

        @NotNull(message = "Thời gian kết thúc không được để trống")
        LocalDateTime endTime,

        @NotNull(message = "Trạng thái không được để trống")
        VoucherStatusEnum status,

        Long version
    ) {}

    public record VoucherUpdateRequest(
        @NotBlank(message = "Mã voucher không được để trống")
        String code,

        @NotNull(message = "Loại giảm giá không được để trống")
        DiscountTypeEnum discountType,

        @NotNull(message = "Giá trị giảm giá không được để trống")
        BigDecimal discountValue,

        BigDecimal minOrderValue,
        
        BigDecimal maxDiscountAmount,

        @NotNull(message = "Tổng số lượng không được để trống")
        Integer totalQuantity,

        @NotNull(message = "Số lượng đã sử dụng không được để trống")
        Integer usedQuantity,

        @NotNull(message = "Thời gian bắt đầu không được để trống")
        LocalDateTime startTime,

        @NotNull(message = "Thời gian kết thúc không được để trống")
        LocalDateTime endTime,

        @NotNull(message = "Trạng thái không được để trống")
        VoucherStatusEnum status,

        Long version
    ) {}
}
