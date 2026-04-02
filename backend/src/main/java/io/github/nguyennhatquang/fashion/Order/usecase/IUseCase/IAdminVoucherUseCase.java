package io.github.nguyennhatquang.fashion.Order.usecase.IUseCase;

import java.util.UUID;

import io.github.nguyennhatquang.fashion.Order.delivery.Dto.Voucher.VoucherRequest.VoucherCreateRequest;
import io.github.nguyennhatquang.fashion.Order.delivery.Dto.Voucher.VoucherRequest.VoucherUpdateRequest;
import io.github.nguyennhatquang.fashion.Order.domain.entity.Voucher;
import io.github.nguyennhatquang.fashion.common.request.ExactPageRequestv2;
import io.github.nguyennhatquang.fashion.common.response.ExactPageResponse;
import io.github.nguyennhatquang.fashion.common.response.Result;

public interface IAdminVoucherUseCase {
    Result<Voucher, Exception> createVoucher(VoucherCreateRequest request);

    Result<Voucher, Exception> updateVoucher(VoucherUpdateRequest request, UUID id);

    Result<Void, Exception> deleteVoucher(UUID id);

    Result<Voucher, Exception> getVoucherById(UUID id);

    Result<ExactPageResponse<Voucher>, Exception> getAllVouchers(ExactPageRequestv2 request);
}
