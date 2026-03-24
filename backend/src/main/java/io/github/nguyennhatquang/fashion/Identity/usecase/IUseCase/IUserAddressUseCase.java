package io.github.nguyennhatquang.fashion.Identity.usecase.IUseCase;

import java.util.List;
import java.util.UUID;

import io.github.nguyennhatquang.fashion.Identity.delivery.dto.UserAddress.UserAddressRequest;
import io.github.nguyennhatquang.fashion.Identity.delivery.dto.UserAddress.UserAddressResponse;
import io.github.nguyennhatquang.fashion.Identity.domain.entity.UserAddress;
import io.github.nguyennhatquang.fashion.common.request.PanigationRequest;
import io.github.nguyennhatquang.fashion.common.response.PanigationResponse;
import io.github.nguyennhatquang.fashion.common.response.Result;

public interface IUserAddressUseCase {
    Result<PanigationResponse<UserAddress>, Exception> GetListUserAddress(UUID UserID, PanigationRequest request);

    Result<UserAddressResponse, Exception> GetDetailUserAddress(UUID id);

    Result<UserAddress, Exception> CreateUserAddress(UUID UserID, UserAddressRequest userAddress);

    Result<UserAddress, Exception> UpdateUserAddress(UUID id, UserAddressRequest userAddress);

    Result<UserAddress, Exception> DeleteUserAddress(UUID id);

}
