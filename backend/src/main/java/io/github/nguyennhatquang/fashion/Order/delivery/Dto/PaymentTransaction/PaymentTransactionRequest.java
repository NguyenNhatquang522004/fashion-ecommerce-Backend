package io.github.nguyennhatquang.fashion.Order.delivery.Dto.PaymentTransaction;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.experimental.UtilityClass;

import io.github.nguyennhatquang.fashion.common.Enum.CurrencyEnum;
import io.github.nguyennhatquang.fashion.common.Enum.PaymentProviderEnum;
import io.github.nguyennhatquang.fashion.common.Enum.PaymentStatusEnum;

import java.math.BigDecimal;
import java.util.UUID;

@UtilityClass
public class PaymentTransactionRequest {
    public record PaymentTransactionCreateRequest(
        @NotNull(message = "Order ID không được để trống")
        UUID orderId,

        @NotNull(message = "Provider không được để trống")
        PaymentProviderEnum provider,

        String providerTransactionId,

        @NotNull(message = "Amount không được để trống")
        BigDecimal amount,

        @NotNull(message = "Currency không được để trống")
        CurrencyEnum currency,

        @NotNull(message = "Status không được để trống")
        PaymentStatusEnum status,

        @NotBlank(message = "Idempotency Key không được để trống")
        String idempotencyKey,

        String errorMessage
    ) {}

    public record PaymentTransactionUpdateRequest(
        @NotNull(message = "Order ID không được để trống")
        UUID orderId,

        @NotNull(message = "Provider không được để trống")
        PaymentProviderEnum provider,

        String providerTransactionId,

        @NotNull(message = "Amount không được để trống")
        BigDecimal amount,

        @NotNull(message = "Currency không được để trống")
        CurrencyEnum currency,

        @NotNull(message = "Status không được để trống")
        PaymentStatusEnum status,

        @NotBlank(message = "Idempotency Key không được để trống")
        String idempotencyKey,

        String errorMessage
    ) {}
}
