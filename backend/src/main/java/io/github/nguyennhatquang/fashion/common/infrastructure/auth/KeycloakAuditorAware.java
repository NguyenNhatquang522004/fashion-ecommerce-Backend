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
        // 1. THỬ LẤY TỪ HTTP REQUEST (REST API)
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()
                && authentication instanceof JwtAuthenticationToken) {
            Jwt jwt = ((JwtAuthenticationToken) authentication).getToken();
            String auditor = jwt.getClaimAsString("userId");
            if (auditor != null && !auditor.trim().isEmpty()) {
                return Optional.of(auditor);
            }
        }

        // 2. KẾT QUẢ TỪ REST API KHÔNG CÓ -> THỬ LẤY TỪ BACKGROUND THREAD (KAFKA
        // CONSUMER)
        String backgroundUserId = UserContextHolder.getUserId();
        if (backgroundUserId != null && !backgroundUserId.trim().isEmpty()) {
            return Optional.of(backgroundUserId);
        }

        // 3. NẾU CẢ 2 NƠI ĐỀU KHÔNG CÓ -> CHẶN ĐỨNG
        throw new RuntimeException("Xác thực không hợp lệ: Thiếu định danh người dùng (userId) cho thao tác DB này.");
    }
}
