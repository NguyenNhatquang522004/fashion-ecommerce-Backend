package io.github.nguyennhatquang.fashion.Identity.delivery.handler;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.nguyennhatquang.fashion.Identity.delivery.dto.ForgetPassword.ForgetPasswordRequest;
import io.github.nguyennhatquang.fashion.Identity.usecase.IUseCase.IForgetPasswordUseCase;
import io.github.nguyennhatquang.fashion.common.response.Result;
import io.github.nguyennhatquang.fashion.common.response.SystemRes;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class ForgetPasswordhandler {
    private final IForgetPasswordUseCase forgetPasswordUseCase;

    @PostMapping("/forget-password")
    public SystemRes forgetPassword(@Validated @RequestBody ForgetPasswordRequest.ForgetPasswordRequestOne request) {
        Result<Void, Exception> result = forgetPasswordUseCase.ForgetPassword(request);
        if (result.hasError()) {
            return SystemRes.builder().status("400").message(result.error().getMessage()).data(null).build();
        }
        return SystemRes.builder().status("200").message("Forget password success").data(null).build();
    }
}
