package io.github.nguyennhatquang.fashion.Identity.usecase.strategyLogin;

import org.springframework.stereotype.Component;

import io.github.nguyennhatquang.fashion.Identity.delivery.dto.login.LoginRequest;
import io.github.nguyennhatquang.fashion.Identity.usecase.IUseCase.ILoginStrategy;
import io.github.nguyennhatquang.fashion.common.Enum.TypeLoginEnum;
import io.github.nguyennhatquang.fashion.common.infrastructure.auth.AuthResponse;
import io.github.nguyennhatquang.fashion.common.shared.IKeycloak;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class FacebookLoginStrategy implements ILoginStrategy {

    private final IKeycloak keycloakAdapter;

    @Override
    public TypeLoginEnum getSupportedType() {
        return TypeLoginEnum.Facebook;
    }

    @Override
    public AuthResponse authenticate(LoginRequest.Command request) {
        // Gọi Keycloak Adapter để lấy token bằng grant_type = password
        return keycloakAdapter.exchangeSocialToken(TypeLoginEnum.Facebook.name(), request.providerToken());
    }
}
