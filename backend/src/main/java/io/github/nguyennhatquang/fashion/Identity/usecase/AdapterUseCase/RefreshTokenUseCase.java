package io.github.nguyennhatquang.fashion.Identity.usecase.AdapterUseCase;

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
        try {
            return keycloak.refreshAccessToken(refreshToken);
        } catch (Exception e) {
            throw new RuntimeException("Refresh token failed", e);
        }
    }
}
