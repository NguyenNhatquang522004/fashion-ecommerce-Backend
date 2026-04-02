package io.github.nguyennhatquang.fashion.Order.usecase.AdapterUseCase;

import java.util.UUID;
import java.util.Set;

import org.springframework.stereotype.Service;

import io.github.nguyennhatquang.fashion.Order.delivery.Dto.PaymentTransaction.PaymentTransactionRequest.PaymentTransactionCreateRequest;
import io.github.nguyennhatquang.fashion.Order.delivery.Dto.PaymentTransaction.PaymentTransactionRequest.PaymentTransactionUpdateRequest;
import io.github.nguyennhatquang.fashion.Order.delivery.Mapper.PaymentTransactionMapper;
import io.github.nguyennhatquang.fashion.Order.domain.IRepository.postgres.IPaymentTransactionRepository;
import io.github.nguyennhatquang.fashion.Order.domain.entity.PaymentTransaction;
import io.github.nguyennhatquang.fashion.Order.usecase.IUseCase.IAdminPaymentTransactionUseCase;
import io.github.nguyennhatquang.fashion.common.infrastructure.Function.JpaExactPagePaginationService;
import io.github.nguyennhatquang.fashion.common.request.ExactPageRequestv2;
import io.github.nguyennhatquang.fashion.common.response.ExactPageResponse;
import io.github.nguyennhatquang.fashion.common.response.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminPaymentTransactionUseCase implements IAdminPaymentTransactionUseCase {
    private final IPaymentTransactionRepository paymentTransactionRepository;
    private final PaymentTransactionMapper paymentTransactionMapper;
    private final JpaExactPagePaginationService paginationService;

    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
            "createdAt", "updatedAt", "amount");

    private static final Set<String> ALLOWED_FILTER_FIELDS = Set.of(
            "orderId", "status", "paymentMethod");

    @Override
    public Result<PaymentTransaction, Exception> createPaymentTransaction(PaymentTransactionCreateRequest request) {
        try {
            PaymentTransaction paymentTransaction = paymentTransactionMapper.toEntity(request);
            paymentTransactionRepository.save(paymentTransaction);
            return Result.success(paymentTransaction);
        } catch (Exception e) {
            log.error("Error creating PaymentTransaction", e);
            return Result.error(e);
        }
    }

    @Override
    public Result<PaymentTransaction, Exception> updatePaymentTransaction(PaymentTransactionUpdateRequest request, UUID id) {
        try {
            PaymentTransaction paymentTransaction = paymentTransactionRepository.findById(id).orElse(null);
            if (paymentTransaction == null) {
                return Result.error(new Exception("PaymentTransaction not found"));
            }
            paymentTransactionMapper.updateEntityFromRequest(request, paymentTransaction);
            paymentTransactionRepository.save(paymentTransaction);
            return Result.success(paymentTransaction);
        } catch (Exception e) {
            log.error("Error updating PaymentTransaction", e);
            return Result.error(e);
        }
    }

    @Override
    public Result<Void, Exception> deletePaymentTransaction(UUID id) {
        try {
            paymentTransactionRepository.deleteById(id);
            return Result.success(null);
        } catch (Exception e) {
            log.error("Error deleting PaymentTransaction", e);
            return Result.error(e);
        }
    }

    @Override
    public Result<PaymentTransaction, Exception> getPaymentTransactionById(UUID id) {
        try {
            PaymentTransaction paymentTransaction = paymentTransactionRepository.findById(id).orElse(null);
            if (paymentTransaction == null) {
                return Result.error(new Exception("PaymentTransaction not found"));
            }
            return Result.success(paymentTransaction);
        } catch (Exception e) {
            log.error("Error getting PaymentTransaction by id", e);
            return Result.error(e);
        }
    }

    @Override
    public Result<ExactPageResponse<PaymentTransaction>, Exception> getAllPaymentTransactions(ExactPageRequestv2 request) {
        try {
            ExactPageResponse<PaymentTransaction> response = paginationService.execute(
                    request,
                    PaymentTransaction.class,
                    ALLOWED_SORT_FIELDS,
                    ALLOWED_FILTER_FIELDS
            );
            return Result.success(response);
        } catch (Exception e) {
            log.error("Error getting all PaymentTransactions", e);
            return Result.error(e);
        }
    }
}
