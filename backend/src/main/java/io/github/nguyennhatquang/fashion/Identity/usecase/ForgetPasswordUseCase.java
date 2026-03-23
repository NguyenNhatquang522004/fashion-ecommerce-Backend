package io.github.nguyennhatquang.fashion.Identity.usecase;

import org.springframework.stereotype.Service;

import io.github.nguyennhatquang.fashion.Identity.delivery.dto.ForgetPassword.ForgetPasswordRequest;
import io.github.nguyennhatquang.fashion.common.response.Result;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ForgetPasswordUseCase implements IForgetPassword {
    private final IRepositoryUserProfile userProfileRepo;
    private final IKeycloak keycloakRepo;

    @Override
    public Result<Void, Exception> ForgetPasswordStepOne(ForgetPasswordRequest.ForgetPasswordRequestOne request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'ForgetPasswordStepOne'");
    }

    @Override
    public Result<Void, Exception> ForgetPasswordStepTwo(ForgetPasswordRequest.ForgetPasswordRequestTwo request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'ForgetPasswordStepTwo'");
    }

}
