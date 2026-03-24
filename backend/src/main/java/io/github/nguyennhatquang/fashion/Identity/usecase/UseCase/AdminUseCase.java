package io.github.nguyennhatquang.fashion.Identity.usecase.UseCase;

import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;

import io.github.nguyennhatquang.fashion.Identity.delivery.dto.Admin.AdminRequest;
import io.github.nguyennhatquang.fashion.Identity.domain.IRepository.postgres.IRepositoryUserProfile;
import io.github.nguyennhatquang.fashion.Identity.domain.entity.UserProfile;
import io.github.nguyennhatquang.fashion.Identity.usecase.IUseCase.IAdminUseCase;
import io.github.nguyennhatquang.fashion.common.request.PanigationRequest;
import io.github.nguyennhatquang.fashion.common.response.Result;
import io.github.nguyennhatquang.fashion.common.shared.IKeycloak;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminUseCase implements IAdminUseCase {
    private final IRepositoryUserProfile userProfileRepo;
    private final IKeycloak keycloakRepo;

    @Override
    public Result<UserProfile, Exception> getAllUser(PanigationRequest panigationRequest) {
        throw new UnsupportedOperationException("Unimplemented method 'getAllUser'");
    }

    @Override
    public Result<UserProfile, Exception> CreatedUser(AdminRequest.CreatedUsers createdUsers) {

        UserProfile user = new UserProfile();
        user.setEmail(createdUsers.email());
        user.setFullName(createdUsers.fullName());
        user.setGender(createdUsers.gender());
        user.setDob(createdUsers.dob());
        user.setPhoneNumber(createdUsers.phoneNumber());
        UserRepresentation userkeycloak = new UserRepresentation();
        userkeycloak.setEmail(createdUsers.email());
        userkeycloak.setFirstName(createdUsers.fullName());
        userkeycloak.setLastName(createdUsers.fullName());
        userkeycloak.setAttributes(null);
        UserRepresentation saveKeycloak = keycloakRepo.updatedOrSaveUser(userkeycloak);
        if (saveKeycloak == null) {
            return Result.error(null);
        }
        user.setKeycloakId(saveKeycloak.getId());
        UserProfile save = userProfileRepo.save(user);
        if (save == null) {
            return Result.error(null);
        }
        return Result.success(save);
    }

    @Override
    public Result<UserProfile, Exception> UpdatedUser(AdminRequest.UpdatedUsers updatedUsers) {
        // TODO Auto-generated method stub
        UserProfile dataUser = userProfileRepo.findbyEmail(updatedUsers.email());
        dataUser.setFullName(updatedUsers.fullName());
        dataUser.setGender(updatedUsers.gender());
        dataUser.setDob(updatedUsers.dob());
        dataUser.setPhoneNumber(updatedUsers.phoneNumber());
        UserRepresentation userkeycloak = new UserRepresentation();
        userkeycloak.setEmail(updatedUsers.email());
        userkeycloak.setFirstName(updatedUsers.fullName());
        userkeycloak.setLastName(updatedUsers.fullName());
        userkeycloak.setAttributes(null);
        UserRepresentation saveKeycloak = keycloakRepo.updatedOrSaveUser(userkeycloak);
        if (saveKeycloak == null) {
            return Result.error(null);
        }
        dataUser.setKeycloakId(saveKeycloak.getId());
        UserProfile save = userProfileRepo.save(dataUser);
        if (save == null) {
            return Result.error(null);
        }
        return Result.success(save);
    }

    @Override
    public Result<UserProfile, Exception> DeletedUser(AdminRequest.DeleteUser deleteUser) {
        // TODO Auto-generated method stub
        UserProfile dataUser = userProfileRepo.findbyEmail(deleteUser.email());
        if (dataUser == null) {
            return Result.error(null);
        }
        keycloakRepo.deleteUserByEmail(deleteUser.email());
        userProfileRepo.delete(dataUser);
        return Result.success(dataUser);
    }
}
