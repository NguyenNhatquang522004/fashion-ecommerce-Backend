package io.github.nguyennhatquang.fashion.Identity.delivery.handler;

import org.springframework.http.ResponseEntity;
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
public class ForgetPasswordHandler {
    private final IForgetPasswordUseCase forgetPasswordUseCase;

    @PostMapping("/forget-password")
    public ResponseEntity<SystemRes> forgetPassword(
            @Validated @RequestBody ForgetPasswordRequest.ForgetPasswordRequestOne request) {
        try {
            Result<Void, Exception> result = forgetPasswordUseCase.ForgetPassword(request);
            if (result.hasError()) {
                return ResponseEntity.badRequest()
                        .body(SystemRes.builder().status("400").message(result.error().getMessage()).data(null).build());
            }
            return ResponseEntity
                    .ok(SystemRes.builder().status("200").message("Forget password success").data(null).build());
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(SystemRes.builder().status("500").message(e.getMessage()).data(null).build());
        }
    }
}
