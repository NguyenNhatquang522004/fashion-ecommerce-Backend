package io.github.nguyennhatquang.fashion.Order.usecase.IUseCase;

import java.util.UUID;

import io.github.nguyennhatquang.fashion.Order.delivery.Dto.UserVoucherWallet.UserVoucherWalletRequest.UserVoucherWalletCreateRequest;
import io.github.nguyennhatquang.fashion.Order.delivery.Dto.UserVoucherWallet.UserVoucherWalletRequest.UserVoucherWalletUpdateRequest;
import io.github.nguyennhatquang.fashion.Order.domain.entity.UserVoucherWallet;
import io.github.nguyennhatquang.fashion.common.request.ExactPageRequestv2;
import io.github.nguyennhatquang.fashion.common.response.ExactPageResponse;
import io.github.nguyennhatquang.fashion.common.response.Result;

public interface IAdminUserVoucherWalletUseCase {
    Result<UserVoucherWallet, Exception> createUserVoucherWallet(UserVoucherWalletCreateRequest request);

    Result<UserVoucherWallet, Exception> updateUserVoucherWallet(UserVoucherWalletUpdateRequest request, UUID id);

    Result<Void, Exception> deleteUserVoucherWallet(UUID id);

    Result<UserVoucherWallet, Exception> getUserVoucherWalletById(UUID id);

    Result<ExactPageResponse<UserVoucherWallet>, Exception> getAllUserVoucherWallets(ExactPageRequestv2 request);
}
