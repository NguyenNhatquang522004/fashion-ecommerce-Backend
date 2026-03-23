package io.github.nguyennhatquang.fashion.common.infrastructure.auth;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component("keycloakAuditorAware") // BẮT BUỘC: Đánh dấu đây là Bean có tên trùng với auditorAwareRef
public class KeycloakAuditorAware implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        return Optional.ofNullable(SecurityContextHolder.getContext())
                .map(SecurityContext::getAuthentication)
                .filter(Authentication::isAuthenticated)
                .filter(auth -> auth instanceof JwtAuthenticationToken)
                .map(auth -> {
                    Jwt jwt = ((JwtAuthenticationToken) auth).getToken();
                    String auditor = jwt.getClaimAsString("preferred_username");
                    // An toàn hơn: Kiểm tra chuỗi rỗng trước khi trả về
                    return (auditor != null && !auditor.trim().isEmpty()) ? auditor : jwt.getSubject();
                })
                .or(() -> Optional.of("SYSTEM")); 
    }
}