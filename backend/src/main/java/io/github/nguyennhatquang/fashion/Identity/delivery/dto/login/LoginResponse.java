package io.github.nguyennhatquang.fashion.Identity.delivery.dto.login;

import java.util.List;

public record LoginResponse(
        String accessToken,
        Long expiresIn,
        String refreshToken,
        Long refreshExpiresIn,
        String tokenType,
        String idToken,
        String sessionState,
        String scope,
        String email,
        String fullName,
        List<String> roles) {

}
