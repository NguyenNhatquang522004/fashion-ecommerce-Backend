package io.github.nguyennhatquang.fashion.common.infrastructure.auth;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.Optional;

public class KeycloakAuditorAware implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        return Optional.ofNullable(SecurityContextHolder.getContext())
                .map(SecurityContext::getAuthentication)
                .filter(Authentication::isAuthenticated)
                .filter(auth -> auth instanceof JwtAuthenticationToken)
                .map(auth -> {
                    Jwt jwt = ((JwtAuthenticationToken) auth).getToken();
                    // Lấy username từ Keycloak, nếu không có thì fallback về Subject ID
                    String auditor = jwt.getClaimAsString("preferred_username");
                    return (auditor != null) ? auditor : jwt.getSubject();
                })
                .or(() -> Optional.of("SYSTEM")); // Mặc định là SYSTEM nếu không có user (VD: Job chạy ngầm)
    }

}
