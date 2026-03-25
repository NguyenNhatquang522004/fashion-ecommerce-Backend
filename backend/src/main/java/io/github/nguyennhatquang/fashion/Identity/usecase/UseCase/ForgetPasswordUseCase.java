package io.github.nguyennhatquang.fashion.Identity.usecase.UseCase;

import org.springframework.stereotype.Service;

import io.github.nguyennhatquang.fashion.Identity.delivery.dto.ForgetPassword.ForgetPasswordRequest;
import io.github.nguyennhatquang.fashion.Identity.domain.IRepository.postgres.IRepositoryUserProfile;
import io.github.nguyennhatquang.fashion.Identity.domain.entity.UserProfile;
import io.github.nguyennhatquang.fashion.Identity.usecase.IUseCase.IForgetPasswordUseCase;
import io.github.nguyennhatquang.fashion.common.Enum.TypeLoginEnum;
import io.github.nguyennhatquang.fashion.common.response.Result;
import io.github.nguyennhatquang.fashion.common.shared.IKeycloak;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ForgetPasswordUseCase implements IForgetPasswordUseCase {
    private final IRepositoryUserProfile userProfileRepo;
    private final IKeycloak keycloakRepo;

    @Override
    public Result<Void, Exception> ForgetPassword(ForgetPasswordRequest.ForgetPasswordRequestOne request) {
        try {
            UserProfile user = userProfileRepo.findbyEmail(request.email());
            if (user == null) {
                return Result.error(new Exception("User not found"));
            }
            if (user.getTypeLogin() != TypeLoginEnum.Local) {
                return Result.error(new Exception("user login social not support"));
            }
            keycloakRepo.sendResetPasswordEmail(user.getKeycloakId());
            return Result.success(null);
        } catch (Exception e) {
            return Result.error(e);
        }
    }

}
