package io.github.nguyennhatquang.fashion.Identity.delivery.dto.ForgetPassword;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ForgetPasswordRequest {
    public record ForgetPasswordRequestOne(
            @NotBlank(message = "Email is required") @Email(message = "Email is invalid") String email) {
    }

    public record ForgetPasswordRequestTwo(
            @NotBlank(message = "Email is required") @Email(message = "Email is invalid") String email,
            @NotBlank(message = "Password is required") String code) {
    }

}
