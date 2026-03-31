package io.github.nguyennhatquang.fashion.Order.delivery.Dto.UserVoucherWallet;

import lombok.Builder;
import lombok.Value;

import io.github.nguyennhatquang.fashion.common.Enum.WalletVoucherStatusEnum;

import java.time.LocalDateTime;
import java.util.UUID;

@Value
@Builder
public class UserVoucherWalletResponse {
    UUID id;
    UUID userId;
    UUID voucherId;
    WalletVoucherStatusEnum status;
    LocalDateTime usedAt;
    String createdBy;
    LocalDateTime createdAt;
    String updatedBy;
    LocalDateTime updatedAt;
}
