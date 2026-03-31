package io.github.nguyennhatquang.fashion.Order.domain.IRepository.postgres;

import io.github.nguyennhatquang.fashion.Order.domain.entity.UserVoucherWallet;
import io.github.nguyennhatquang.fashion.common.request.ExactPageRequest;
import io.github.nguyennhatquang.fashion.common.response.ExactPageResponse;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IUserVoucherWalletRepository {
    UserVoucherWallet save(UserVoucherWallet entity);

    List<UserVoucherWallet> saveAll(List<UserVoucherWallet> entities);

    UserVoucherWallet update(UserVoucherWallet entity);

    List<UserVoucherWallet> updateAll(List<UserVoucherWallet> entities);

    void delete(UserVoucherWallet entity);

    void deleteById(UUID id);

    void deleteAll(List<UserVoucherWallet> entities);

    void softDeleteById(UUID id);

    Optional<UserVoucherWallet> findById(UUID id);

    List<UserVoucherWallet> findAll();

    ExactPageResponse<UserVoucherWallet> getUserVoucherWalletExactPage(ExactPageRequest request);
}
