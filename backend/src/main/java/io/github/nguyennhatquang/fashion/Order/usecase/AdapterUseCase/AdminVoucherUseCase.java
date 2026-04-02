package io.github.nguyennhatquang.fashion.Order.usecase.AdapterUseCase;

import java.util.UUID;
import java.util.Set;

import org.springframework.stereotype.Service;

import io.github.nguyennhatquang.fashion.Order.delivery.Dto.Voucher.VoucherRequest.VoucherCreateRequest;
import io.github.nguyennhatquang.fashion.Order.delivery.Dto.Voucher.VoucherRequest.VoucherUpdateRequest;
import io.github.nguyennhatquang.fashion.Order.delivery.Mapper.VoucherMapper;
import io.github.nguyennhatquang.fashion.Order.domain.IRepository.postgres.IVoucherRepository;
import io.github.nguyennhatquang.fashion.Order.domain.entity.Voucher;
import io.github.nguyennhatquang.fashion.Order.usecase.IUseCase.IAdminVoucherUseCase;
import io.github.nguyennhatquang.fashion.common.infrastructure.Function.JpaExactPagePaginationService;
import io.github.nguyennhatquang.fashion.common.request.ExactPageRequestv2;
import io.github.nguyennhatquang.fashion.common.response.ExactPageResponse;
import io.github.nguyennhatquang.fashion.common.response.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminVoucherUseCase implements IAdminVoucherUseCase {
    private final IVoucherRepository voucherRepository;
    private final VoucherMapper voucherMapper;
    private final JpaExactPagePaginationService paginationService;

    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
            "createdAt", "updatedAt", "startTime", "endTime");

    private static final Set<String> ALLOWED_FILTER_FIELDS = Set.of(
            "code", "status", "discountType");

    @Override
    public Result<Voucher, Exception> createVoucher(VoucherCreateRequest request) {
        try {
            Voucher voucher = voucherMapper.toEntity(request);
            voucherRepository.save(voucher);
            return Result.success(voucher);
        } catch (Exception e) {
            log.error("Error creating Voucher", e);
            return Result.error(e);
        }
    }

    @Override
    public Result<Voucher, Exception> updateVoucher(VoucherUpdateRequest request, UUID id) {
        try {
            Voucher voucher = voucherRepository.findById(id).orElse(null);
            if (voucher == null) {
                return Result.error(new Exception("Voucher not found"));
            }
            voucherMapper.updateEntityFromRequest(request, voucher);
            voucherRepository.save(voucher);
            return Result.success(voucher);
        } catch (Exception e) {
            log.error("Error updating Voucher", e);
            return Result.error(e);
        }
    }

    @Override
    public Result<Void, Exception> deleteVoucher(UUID id) {
        try {
            voucherRepository.deleteById(id);
            return Result.success(null);
        } catch (Exception e) {
            log.error("Error deleting Voucher", e);
            return Result.error(e);
        }
    }

    @Override
    public Result<Voucher, Exception> getVoucherById(UUID id) {
        try {
            Voucher voucher = voucherRepository.findById(id).orElse(null);
            if (voucher == null) {
                return Result.error(new Exception("Voucher not found"));
            }
            return Result.success(voucher);
        } catch (Exception e) {
            log.error("Error getting Voucher by id", e);
            return Result.error(e);
        }
    }

    @Override
    public Result<ExactPageResponse<Voucher>, Exception> getAllVouchers(ExactPageRequestv2 request) {
        try {
            ExactPageResponse<Voucher> response = paginationService.execute(
                    request,
                    Voucher.class,
                    ALLOWED_SORT_FIELDS,
                    ALLOWED_FILTER_FIELDS
            );
            return Result.success(response);
        } catch (Exception e) {
            log.error("Error getting all Vouchers", e);
            return Result.error(e);
        }
    }
}
