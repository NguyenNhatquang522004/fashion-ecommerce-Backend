package io.github.nguyennhatquang.fashion.Identity.usecase.IUseCase;

import io.github.nguyennhatquang.fashion.Identity.delivery.dto.register.RegisterRequest;
import io.github.nguyennhatquang.fashion.Identity.domain.entity.UserProfile;
import io.github.nguyennhatquang.fashion.common.response.Result;

public interface IRegisterUseCase {

    Result<UserProfile, Exception> RegisterStepOne(RegisterRequest.RegisterStepOne request);

    Result<UserProfile, Exception> RegisterStepTwo(RegisterRequest.RegisterStepTwo request);

    Result<UserProfile, Exception> RegisterStepThree(RegisterRequest.RegisterStepThree request);

    Result<UserProfile, Exception> RegisterStepFour(RegisterRequest.RegisterStepFour request);

    Result<UserProfile, Exception> ResendOTP(String email);
}
