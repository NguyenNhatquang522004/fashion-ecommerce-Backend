package io.github.nguyennhatquang.fashion.Order.delivery.Dto.UserVoucherWallet;

import jakarta.validation.constraints.NotNull;
import lombok.experimental.UtilityClass;

import io.github.nguyennhatquang.fashion.common.Enum.WalletVoucherStatusEnum;

import java.time.LocalDateTime;
import java.util.UUID;

@UtilityClass
public class UserVoucherWalletRequest {
    public record UserVoucherWalletCreateRequest(
        @NotNull(message = "User ID không được để trống")
        UUID userId,

        @NotNull(message = "Voucher ID không được để trống")
        UUID voucherId,

        @NotNull(message = "Status không được để trống")
        WalletVoucherStatusEnum status,

        LocalDateTime usedAt
    ) {}

    public record UserVoucherWalletUpdateRequest(
        @NotNull(message = "User ID không được để trống")
        UUID userId,

        @NotNull(message = "Voucher ID không được để trống")
        UUID voucherId,

        @NotNull(message = "Status không được để trống")
        WalletVoucherStatusEnum status,

        LocalDateTime usedAt
    ) {}
}
