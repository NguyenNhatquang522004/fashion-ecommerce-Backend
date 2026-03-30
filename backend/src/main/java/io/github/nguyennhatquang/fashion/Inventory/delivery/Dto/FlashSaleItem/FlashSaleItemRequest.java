package io.github.nguyennhatquang.fashion.Inventory.delivery.Dto.FlashSaleItem;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.util.UUID;

@UtilityClass
public class FlashSaleItemRequest {

    public record FlashSaleItemCreateRequest(
            @NotNull(message = "Campaign ID là bắt buộc")
            UUID campaignId,

            @NotBlank(message = "SKU code không được để trống")
            String skuCode,

            @NotNull(message = "Giá khuyến mãi là bắt buộc")
            @DecimalMin(value = "0.0", inclusive = false, message = "Giá khuyến mãi phải lớn hơn 0")
            BigDecimal promotionalPrice,

            @NotNull(message = "Tổng quota là bắt buộc")
            @Min(value = 1, message = "Tổng quota phải lớn hơn 0")
            Integer totalQuota,

            @Min(value = 1, message = "Giới hạn mua mỗi khách hàng phải ít nhất là 1")
            Integer purchaseLimitPerUser
    ) {}

    public record FlashSaleItemUpdateRequest(
            @NotNull(message = "Giá khuyến mãi là bắt buộc")
            @DecimalMin(value = "0.0", inclusive = false, message = "Giá khuyến mãi phải lớn hơn 0")
            BigDecimal promotionalPrice,

            @NotNull(message = "Tổng quota là bắt buộc")
            @Min(value = 1, message = "Tổng quota phải lớn hơn 0")
            Integer totalQuota,

            @Min(value = 1, message = "Giới hạn mua mỗi khách hàng phải ít nhất là 1")
            Integer purchaseLimitPerUser
    ) {}
}
