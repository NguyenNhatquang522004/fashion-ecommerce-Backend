package io.github.nguyennhatquang.fashion.Order.domain.IRepository.postgres;

import io.github.nguyennhatquang.fashion.Order.domain.entity.PaymentTransaction;
import io.github.nguyennhatquang.fashion.common.request.ExactPageRequest;
import io.github.nguyennhatquang.fashion.common.response.ExactPageResponse;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IPaymentTransactionRepository {
    PaymentTransaction save(PaymentTransaction entity);

    List<PaymentTransaction> saveAll(List<PaymentTransaction> entities);

    PaymentTransaction update(PaymentTransaction entity);

    List<PaymentTransaction> updateAll(List<PaymentTransaction> entities);

    void delete(PaymentTransaction entity);

    void deleteById(UUID id);

    void deleteAll(List<PaymentTransaction> entities);

    void softDeleteById(UUID id);

    Optional<PaymentTransaction> findById(UUID id);

    List<PaymentTransaction> findAll();

    ExactPageResponse<PaymentTransaction> getPaymentTransactionExactPage(ExactPageRequest request);
}
