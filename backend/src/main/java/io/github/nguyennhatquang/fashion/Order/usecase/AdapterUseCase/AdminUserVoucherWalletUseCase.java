package io.github.nguyennhatquang.fashion.Order.usecase.AdapterUseCase;

import java.util.UUID;
import java.util.Set;

import org.springframework.stereotype.Service;

import io.github.nguyennhatquang.fashion.Order.delivery.Dto.UserVoucherWallet.UserVoucherWalletRequest.UserVoucherWalletCreateRequest;
import io.github.nguyennhatquang.fashion.Order.delivery.Dto.UserVoucherWallet.UserVoucherWalletRequest.UserVoucherWalletUpdateRequest;
import io.github.nguyennhatquang.fashion.Order.delivery.Mapper.UserVoucherWalletMapper;
import io.github.nguyennhatquang.fashion.Order.domain.IRepository.postgres.IUserVoucherWalletRepository;
import io.github.nguyennhatquang.fashion.Order.domain.entity.UserVoucherWallet;
import io.github.nguyennhatquang.fashion.Order.usecase.IUseCase.IAdminUserVoucherWalletUseCase;
import io.github.nguyennhatquang.fashion.common.infrastructure.Function.JpaExactPagePaginationService;
import io.github.nguyennhatquang.fashion.common.request.ExactPageRequestv2;
import io.github.nguyennhatquang.fashion.common.response.ExactPageResponse;
import io.github.nguyennhatquang.fashion.common.response.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminUserVoucherWalletUseCase implements IAdminUserVoucherWalletUseCase {
    private final IUserVoucherWalletRepository userVoucherWalletRepository;
    private final UserVoucherWalletMapper userVoucherWalletMapper;
    private final JpaExactPagePaginationService paginationService;

    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
            "createdAt", "updatedAt");

    private static final Set<String> ALLOWED_FILTER_FIELDS = Set.of(
            "userId", "voucherId", "status");

    @Override
    public Result<UserVoucherWallet, Exception> createUserVoucherWallet(UserVoucherWalletCreateRequest request) {
        try {
            UserVoucherWallet userVoucherWallet = userVoucherWalletMapper.toEntity(request);
            userVoucherWalletRepository.save(userVoucherWallet);
            return Result.success(userVoucherWallet);
        } catch (Exception e) {
            log.error("Error creating UserVoucherWallet", e);
            return Result.error(e);
        }
    }

    @Override
    public Result<UserVoucherWallet, Exception> updateUserVoucherWallet(UserVoucherWalletUpdateRequest request, UUID id) {
        try {
            UserVoucherWallet userVoucherWallet = userVoucherWalletRepository.findById(id).orElse(null);
            if (userVoucherWallet == null) {
                return Result.error(new Exception("UserVoucherWallet not found"));
            }
            userVoucherWalletMapper.updateEntityFromRequest(request, userVoucherWallet);
            userVoucherWalletRepository.save(userVoucherWallet);
            return Result.success(userVoucherWallet);
        } catch (Exception e) {
            log.error("Error updating UserVoucherWallet", e);
            return Result.error(e);
        }
    }

    @Override
    public Result<Void, Exception> deleteUserVoucherWallet(UUID id) {
        try {
            userVoucherWalletRepository.deleteById(id);
            return Result.success(null);
        } catch (Exception e) {
            log.error("Error deleting UserVoucherWallet", e);
            return Result.error(e);
        }
    }

    @Override
    public Result<UserVoucherWallet, Exception> getUserVoucherWalletById(UUID id) {
        try {
            UserVoucherWallet userVoucherWallet = userVoucherWalletRepository.findById(id).orElse(null);
            if (userVoucherWallet == null) {
                return Result.error(new Exception("UserVoucherWallet not found"));
            }
            return Result.success(userVoucherWallet);
        } catch (Exception e) {
            log.error("Error getting UserVoucherWallet by id", e);
            return Result.error(e);
        }
    }

    @Override
    public Result<ExactPageResponse<UserVoucherWallet>, Exception> getAllUserVoucherWallets(ExactPageRequestv2 request) {
        try {
            ExactPageResponse<UserVoucherWallet> response = paginationService.execute(
                    request,
                    UserVoucherWallet.class,
                    ALLOWED_SORT_FIELDS,
                    ALLOWED_FILTER_FIELDS
            );
            return Result.success(response);
        } catch (Exception e) {
            log.error("Error getting all UserVoucherWallets", e);
            return Result.error(e);
        }
    }
}
