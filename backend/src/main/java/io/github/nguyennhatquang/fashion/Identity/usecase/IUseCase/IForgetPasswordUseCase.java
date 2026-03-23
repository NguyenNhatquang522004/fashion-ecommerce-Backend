package io.github.nguyennhatquang.fashion.Identity.usecase.IUseCase;

import io.github.nguyennhatquang.fashion.Identity.delivery.dto.ForgetPassword.ForgetPasswordRequest.ForgetPasswordRequestOne;
import io.github.nguyennhatquang.fashion.common.response.Result;

public interface IForgetPasswordUseCase {
    Result<Void, Exception> ForgetPassword(ForgetPasswordRequestOne request);

}
