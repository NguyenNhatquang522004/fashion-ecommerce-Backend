package io.github.nguyennhatquang.fashion.Identity.usecase.AdapterUseCase;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import io.github.nguyennhatquang.fashion.Identity.delivery.dto.UserAddress.UserAddressRequest;
import io.github.nguyennhatquang.fashion.Identity.delivery.dto.UserAddress.UserAddressResponse;
import io.github.nguyennhatquang.fashion.Identity.delivery.mapper.UserAddressMapper;
import io.github.nguyennhatquang.fashion.Identity.domain.IRepository.postgres.IRepositoryUserAddress;
import io.github.nguyennhatquang.fashion.Identity.domain.IRepository.postgres.IRepositoryUserProfile;
import io.github.nguyennhatquang.fashion.Identity.domain.entity.UserAddress;
import io.github.nguyennhatquang.fashion.Identity.domain.entity.UserProfile;
import io.github.nguyennhatquang.fashion.Identity.usecase.IUseCase.IUserAddressUseCase;
import io.github.nguyennhatquang.fashion.common.request.PanigationRequest;
import io.github.nguyennhatquang.fashion.common.response.PanigationResponse;
import io.github.nguyennhatquang.fashion.common.response.Result;
import io.github.nguyennhatquang.fashion.common.shared.IKeycloak;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserAddressUseCase implements IUserAddressUseCase {
    private final IRepositoryUserProfile userprofilerepo;
    private final IRepositoryUserAddress userAddressRepo;
    private final UserAddressMapper mapper;
    private final IKeycloak keycloakRepo;

    @Override
    public Result<PanigationResponse<UserAddress>, Exception> GetListUserAddress(UUID UserID,
            PanigationRequest request) {
        try {
            PanigationResponse<UserAddress> response = userAddressRepo.getAddressesCursor(UserID, request);
            return Result.success(response);
        } catch (Exception e) {
            return Result.error(e);
        }
    }

    @Override
    public Result<UserAddressResponse, Exception> GetDetailUserAddress(UUID id) {
        try {
            Optional<UserAddress> data = userAddressRepo.findById(id);
            if (data.isEmpty()) {
                return Result.error(new Exception("User address not found"));
            }
            UserAddressResponse userAddressResponse = mapper.toResponse(data.get());
            return Result.success(userAddressResponse);
        } catch (Exception e) {
            return Result.error(e);
        }

    }

    @Override
    public Result<UserAddress, Exception> CreateUserAddress(UUID UserID, UserAddressRequest userAddress) {
        // TODO Auto-generated method stub
        try {
            UserProfile user = userprofilerepo.findById(UserID);
            if (user == null) {
                return Result.error(new Exception("User not found"));
            }
            UserAddress userAddressa = mapper.toEntity(userAddress);
            userAddressa.setUserProfile(user);
            userAddressRepo.save(userAddressa);
            return Result.success(userAddressa);
        } catch (Exception e) {
            return Result.error(e);
        }
    }

    @Override
    public Result<UserAddress, Exception> UpdateUserAddress(UUID id, UserAddressRequest userAddress) {
        // TODO Auto-generated method stub

        try {
            Optional<UserAddress> useraddress = userAddressRepo.findById(id);
            if (useraddress.isEmpty()) {
                return Result.error(new Exception("User address not found"));
            }
            mapper.updateEntityFromRequest(userAddress, useraddress.get());
            userAddressRepo.save(useraddress.get());
            return Result.success(useraddress.get());
        } catch (Exception e) {
            return Result.error(e);
        }
    }

    @Override
    public Result<UserAddress, Exception> DeleteUserAddress(UUID id) {
        // TODO Auto-generated method stub
        try {
            Optional<UserAddress> useraddress = userAddressRepo.findById(id);
            if (useraddress.isEmpty()) {
                return Result.error(new Exception("User address not found"));
            }
            userAddressRepo.delete(useraddress.get());
            return Result.success(useraddress.get());
        } catch (Exception e) {
            return Result.error(e);
        }
    }

}
