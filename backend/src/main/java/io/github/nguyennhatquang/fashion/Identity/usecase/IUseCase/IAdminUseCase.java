package io.github.nguyennhatquang.fashion.Identity.usecase.IUseCase;

import io.github.nguyennhatquang.fashion.Identity.delivery.dto.Admin.AdminRequest;
import io.github.nguyennhatquang.fashion.Identity.domain.entity.UserProfile;
import io.github.nguyennhatquang.fashion.common.request.PanigationRequest;
import io.github.nguyennhatquang.fashion.common.response.Result;

public interface IAdminUseCase {

    Result<UserProfile, Exception> getAllUser(PanigationRequest panigationRequest);

    Result<UserProfile, Exception> CreatedUser(AdminRequest.CreatedUsers createdUsers);

    Result<UserProfile, Exception> UpdatedUser(AdminRequest.UpdatedUsers updatedUsers);

    Result<UserProfile, Exception> DeletedUser(AdminRequest.DeleteUser deleteUser);

}
