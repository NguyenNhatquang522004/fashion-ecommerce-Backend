package io.github.nguyennhatquang.fashion.Order.delivery.Dto.Voucher;

import lombok.Builder;
import lombok.Value;

import io.github.nguyennhatquang.fashion.common.Enum.DiscountTypeEnum;
import io.github.nguyennhatquang.fashion.common.Enum.VoucherStatusEnum;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Value
@Builder
public class VoucherResponse {
    UUID id;
    String code;
    DiscountTypeEnum discountType;
    BigDecimal discountValue;
    BigDecimal minOrderValue;
    BigDecimal maxDiscountAmount;
    Integer totalQuantity;
    Integer usedQuantity;
    LocalDateTime startTime;
    LocalDateTime endTime;
    VoucherStatusEnum status;
    Long version;
    String createdBy;
    LocalDateTime createdAt;
    String updatedBy;
    LocalDateTime updatedAt;
}
