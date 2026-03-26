package io.github.nguyennhatquang.fashion.Identity.usecase.AdapterUseCase;

import java.util.UUID;

import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;

import io.github.nguyennhatquang.fashion.Identity.delivery.dto.Admin.AdminRequest;
import io.github.nguyennhatquang.fashion.Identity.domain.IRepository.postgres.IRepositoryUserProfile;
import io.github.nguyennhatquang.fashion.Identity.domain.entity.UserProfile;
import io.github.nguyennhatquang.fashion.Identity.usecase.IUseCase.IAdminUseCase;
import io.github.nguyennhatquang.fashion.common.request.ExactPageRequest;
import io.github.nguyennhatquang.fashion.common.response.ExactPageResponse;
import io.github.nguyennhatquang.fashion.common.response.Result;
import io.github.nguyennhatquang.fashion.common.shared.IKeycloak;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminUseCase implements IAdminUseCase {
    private final IRepositoryUserProfile userProfileRepo;
    private final IKeycloak keycloakRepo;

    @Override
    public Result<UserProfile, Exception> GetDetailUser(UUID id) {
        try {
            UserProfile data = userProfileRepo.findById(id);
            if (data == null) {
                return Result.error(new Exception("User not found"));
            }
            return Result.success(data);
        } catch (Exception e) {
            return Result.error(e);
        }
    }

    @Override
    public Result<ExactPageResponse<UserProfile>, Exception> getAllUser(ExactPageRequest panigationRequest) {
        try {
            ExactPageResponse<UserProfile> data = userProfileRepo.getProfilesExactPage(panigationRequest);
            return Result.success(data);
        } catch (Exception e) {
            return Result.error(e);
        }
    }

    @Override
    public Result<UserProfile, Exception> CreatedUser(AdminRequest.CreatedUsers createdUsers) {
        try {
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
                return Result.error(new Exception("Failed to save user to Keycloak"));
            }

            user.setKeycloakId(saveKeycloak.getId());
            UserProfile save = userProfileRepo.save(user);
            if (save == null) {
                return Result.error(new Exception("Failed to save user profile"));
            }
            return Result.success(save);
        } catch (Exception e) {
            return Result.error(e);
        }
    }

    @Override
    public Result<UserProfile, Exception> UpdatedUser(AdminRequest.UpdatedUsers updatedUsers) {
        try {
            UserProfile dataUser = userProfileRepo.findbyEmail(updatedUsers.email());
            if (dataUser == null) {
                return Result.error(new Exception("User not found"));
            }
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
                return Result.error(new Exception("Failed to update user in Keycloak"));
            }
            dataUser.setKeycloakId(saveKeycloak.getId());
            UserProfile save = userProfileRepo.save(dataUser);
            if (save == null) {
                return Result.error(new Exception("Failed to update user profile"));
            }
            keycloakRepo.syncUserRoles(saveKeycloak.getId(), updatedUsers.roles());
            return Result.success(save);
        } catch (Exception e) {
            return Result.error(e);
        }
    }

    @Override
    public Result<UserProfile, Exception> DeletedUser(AdminRequest.DeleteUser deleteUser) {
        try {
            UserProfile dataUser = userProfileRepo.findbyEmail(deleteUser.email());
            if (dataUser == null) {
                return Result.error(new Exception("User not found"));
            }
            keycloakRepo.deleteUserByEmail(deleteUser.email());
            userProfileRepo.delete(dataUser);
            return Result.success(dataUser);
        } catch (Exception e) {
            return Result.error(e);
        }
    }
}
