package io.github.nguyennhatquang.fashion.Order.delivery.Dto.PaymentTransaction;

import lombok.Builder;
import lombok.Value;

import io.github.nguyennhatquang.fashion.common.Enum.CurrencyEnum;
import io.github.nguyennhatquang.fashion.common.Enum.PaymentProviderEnum;
import io.github.nguyennhatquang.fashion.common.Enum.PaymentStatusEnum;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Value
@Builder
public class PaymentTransactionResponse {
    UUID id;
    UUID orderId;
    PaymentProviderEnum provider;
    String providerTransactionId;
    BigDecimal amount;
    CurrencyEnum currency;
    PaymentStatusEnum status;
    String idempotencyKey;
    String errorMessage;
    String createdBy;
    LocalDateTime createdAt;
    String updatedBy;
    LocalDateTime updatedAt;
}
