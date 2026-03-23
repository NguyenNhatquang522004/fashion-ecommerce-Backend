package io.github.nguyennhatquang.fashion.Identity.usecase.IUseCase;

import io.github.nguyennhatquang.fashion.common.infrastructure.auth.AuthResponse;

public interface IRefreshTokenUseCase {
    AuthResponse execute(String refreshToken);
}
