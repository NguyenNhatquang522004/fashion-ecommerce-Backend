package io.github.nguyennhatquang.fashion.Identity.delivery.dto.login;

import io.github.nguyennhatquang.fashion.common.Enum.TypeLoginEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.experimental.UtilityClass;

@UtilityClass
public class LoginRequest {

    public record Command(
            @NotNull(message = "Loại đăng nhập không được để trống") TypeLoginEnum loginType,

            // Dùng cho Local Login
            String email,
            String password,

            // Dùng cho Social Login (Google, Facebook...)
            String providerToken) {
        // Validation nội bộ (Self-validating DTO - Clean Architecture)
        public Command {
            if (loginType == TypeLoginEnum.Local) {
                if (email == null || email.isBlank())
                    throw new IllegalArgumentException("Email không được trống với Local Login");
                if (password == null || password.isBlank())
                    throw new IllegalArgumentException("Password không được trống với Local Login");
            } else {
                if (providerToken == null || providerToken.isBlank()) {
                    throw new IllegalArgumentException("Provider Token không được trống với Social Login");
                }
            }
        }
    }
}