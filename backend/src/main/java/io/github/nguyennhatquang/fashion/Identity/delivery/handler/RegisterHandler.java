package io.github.nguyennhatquang.fashion.Identity.delivery.handler;

import io.github.nguyennhatquang.fashion.Identity.usecase.RegisterUseCase;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import io.github.nguyennhatquang.fashion.Identity.delivery.dto.register.RegisterRequest;
import io.github.nguyennhatquang.fashion.Identity.domain.entity.UserProfile;
import io.github.nguyennhatquang.fashion.Identity.usecase.IRegisterUseCase;
import io.github.nguyennhatquang.fashion.common.response.Result;
import io.github.nguyennhatquang.fashion.common.response.SystemRes;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class RegisterHandler {
    private final IRegisterUseCase registerService;

    @PostMapping("/register/stepone")
    public SystemRes RegisterStepOne(
            @Validated @RequestBody RegisterRequest.RegisterStepOne request) {
        Result<UserProfile, Exception> result = registerService.RegisterStepOne(request);
        if (result.hasError()) {
            return SystemRes.error(result.error().getMessage(), "400");
        }
        return SystemRes.builder()
                .data(result.data().getEmail())
                .code(HttpStatus.OK.toString())
                .status(HttpStatus.OK.name())
                .message("Register step one success").build();
    }

    @PostMapping("/register/steptwo")
    public SystemRes RegisterStepTwo(
            @Validated @RequestBody RegisterRequest.RegisterStepTwo request) {
        Result<UserProfile, Exception> result = registerService.RegisterStepTwo(request);
        if (result.hasError()) {
            return SystemRes.error(result.error().getMessage(), "400");
        }
        return SystemRes.builder()
                .data(result.data().getEmail())
                .code(HttpStatus.OK.toString())
                .status(HttpStatus.OK.name())
                .message("Register step two success").build();
    }

    @PostMapping("/register/stepthree")
    public SystemRes RegisterStepThree(
            @Validated @RequestBody RegisterRequest.RegisterStepThree request) {
        Result<UserProfile, Exception> result = registerService.RegisterStepThree(request);
        if (result.hasError()) {
            return SystemRes.error(result.error().getMessage(), "400");
        }
        return SystemRes.builder()
                .data(result.data().getEmail())
                .code(HttpStatus.OK.toString())
                .status(HttpStatus.OK.name())
                .message("Register step three success").build();
    }

    @PostMapping("/register/stepfour")
    public SystemRes RegisterStepFour(
            @Validated @RequestBody RegisterRequest.RegisterStepFour request) {
        Result<UserProfile, Exception> result = registerService.RegisterStepFour(request);
        if (result.hasError()) {
            return SystemRes.error(result.error().getMessage(), "400");
        }
        return SystemRes.builder()
                .data(result.data().getEmail())
                .code(HttpStatus.OK.toString())
                .status(HttpStatus.OK.name())
                .message("Register step four success").build();
    }

}
