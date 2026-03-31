package io.github.nguyennhatquang.fashion.Order.delivery.Mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import io.github.nguyennhatquang.fashion.Order.delivery.Dto.UserVoucherWallet.UserVoucherWalletRequest.UserVoucherWalletCreateRequest;
import io.github.nguyennhatquang.fashion.Order.delivery.Dto.UserVoucherWallet.UserVoucherWalletRequest.UserVoucherWalletUpdateRequest;
import io.github.nguyennhatquang.fashion.Order.delivery.Dto.UserVoucherWallet.UserVoucherWalletResponse;
import io.github.nguyennhatquang.fashion.Order.domain.entity.UserVoucherWallet;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserVoucherWalletMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isDeleted", constant = "false")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    UserVoucherWallet toEntity(UserVoucherWalletCreateRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    void updateEntityFromRequest(UserVoucherWalletUpdateRequest request, @MappingTarget UserVoucherWallet entity);

    UserVoucherWalletResponse toResponse(UserVoucherWallet entity);

    List<UserVoucherWalletResponse> toResponseList(List<UserVoucherWallet> entities);
}
