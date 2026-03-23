package io.github.nguyennhatquang.fashion.Identity.delivery.dto.register;

import lombok.experimental.UtilityClass;

@UtilityClass
public class RegisterResponse {
    public record RegisterStepOne(
        String email
    ) {}

    public record RegisterStepTwo(
        String UserName ,
        String password
    ) {}

    public record RegisterStepThree(
        String fullName,
        String phoneNumber,
        String address
    ) {}
}
