package io.github.nguyennhatquang.fashion.Identity.usecase.UseCase;

import org.springframework.stereotype.Service;

import io.github.nguyennhatquang.fashion.Identity.usecase.IUseCase.IRefreshTokenUseCase;
import io.github.nguyennhatquang.fashion.common.infrastructure.auth.AuthResponse;
import io.github.nguyennhatquang.fashion.common.shared.IKeycloak;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RefreshTokenUseCase implements IRefreshTokenUseCase {
    private final IKeycloak keycloak;

    @Override
    public AuthResponse execute(String refreshToken) {
        return keycloak.refreshAccessToken(refreshToken);
    }
}
