package io.github.nguyennhatquang.fashion.Identity.usecase;



import io.github.nguyennhatquang.fashion.Identity.delivery.dto.login.LoginRequest;
import io.github.nguyennhatquang.fashion.common.Enum.TypeLoginEnum;
import io.github.nguyennhatquang.fashion.common.infrastructure.auth.AuthResponse;
import io.github.nguyennhatquang.fashion.common.response.Result;

public interface ILoginStrategy {
    TypeLoginEnum getSupportedType();

    AuthResponse authenticate(LoginRequest.Command request);
}
