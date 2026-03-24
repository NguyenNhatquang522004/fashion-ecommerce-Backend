package io.github.nguyennhatquang.fashion.Identity.usecase.IUseCase;

import java.util.UUID;

import io.github.nguyennhatquang.fashion.Identity.delivery.dto.Admin.AdminRequest;
import io.github.nguyennhatquang.fashion.Identity.domain.entity.UserProfile;
import io.github.nguyennhatquang.fashion.common.request.ExactPageRequest;
import io.github.nguyennhatquang.fashion.common.response.ExactPageResponse;
import io.github.nguyennhatquang.fashion.common.response.Result;

public interface IAdminUseCase {
    Result<UserProfile, Exception> GetDetailUser(UUID id);

    Result<ExactPageResponse<UserProfile>, Exception> getAllUser(ExactPageRequest exactPageRequest);

    Result<UserProfile, Exception> CreatedUser(AdminRequest.CreatedUsers createdUsers);

    Result<UserProfile, Exception> UpdatedUser(AdminRequest.UpdatedUsers updatedUsers);

    Result<UserProfile, Exception> DeletedUser(AdminRequest.DeleteUser deleteUser);

}
