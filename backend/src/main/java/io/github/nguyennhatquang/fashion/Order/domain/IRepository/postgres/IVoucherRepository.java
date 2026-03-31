package io.github.nguyennhatquang.fashion.Order.domain.IRepository.postgres;

import io.github.nguyennhatquang.fashion.Order.domain.entity.Voucher;
import io.github.nguyennhatquang.fashion.common.request.ExactPageRequest;
import io.github.nguyennhatquang.fashion.common.response.ExactPageResponse;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IVoucherRepository {
    Voucher save(Voucher entity);

    List<Voucher> saveAll(List<Voucher> entities);

    Voucher update(Voucher entity);

    List<Voucher> updateAll(List<Voucher> entities);

    void delete(Voucher entity);

    void deleteById(UUID id);

    void deleteAll(List<Voucher> entities);

    void softDeleteById(UUID id);

    Optional<Voucher> findById(UUID id);

    List<Voucher> findAll();

    ExactPageResponse<Voucher> getVoucherExactPage(ExactPageRequest request);
}
