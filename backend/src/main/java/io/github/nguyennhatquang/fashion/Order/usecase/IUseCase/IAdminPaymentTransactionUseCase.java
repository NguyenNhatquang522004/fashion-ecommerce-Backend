package io.github.nguyennhatquang.fashion.Order.usecase.IUseCase;

import java.util.UUID;

import io.github.nguyennhatquang.fashion.Order.delivery.Dto.PaymentTransaction.PaymentTransactionRequest.PaymentTransactionCreateRequest;
import io.github.nguyennhatquang.fashion.Order.delivery.Dto.PaymentTransaction.PaymentTransactionRequest.PaymentTransactionUpdateRequest;
import io.github.nguyennhatquang.fashion.Order.domain.entity.PaymentTransaction;
import io.github.nguyennhatquang.fashion.common.request.ExactPageRequestv2;
import io.github.nguyennhatquang.fashion.common.response.ExactPageResponse;
import io.github.nguyennhatquang.fashion.common.response.Result;

public interface IAdminPaymentTransactionUseCase {
    Result<PaymentTransaction, Exception> createPaymentTransaction(PaymentTransactionCreateRequest request);

    Result<PaymentTransaction, Exception> updatePaymentTransaction(PaymentTransactionUpdateRequest request, UUID id);

    Result<Void, Exception> deletePaymentTransaction(UUID id);

    Result<PaymentTransaction, Exception> getPaymentTransactionById(UUID id);

    Result<ExactPageResponse<PaymentTransaction>, Exception> getAllPaymentTransactions(ExactPageRequestv2 request);
}
