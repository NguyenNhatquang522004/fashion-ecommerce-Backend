package io.github.nguyennhatquang.fashion.Identity.usecase;

import io.github.nguyennhatquang.fashion.common.response.Result;

public interface IForgetPasswordUseCase {
    Result<Void, Exception> ForgetPasswordStepOne(ForgetPasswordRequestOne request);

    Result<Void, Exception> ForgetPasswordStepTwo(ForgetPasswordRequestTwo request);
}
