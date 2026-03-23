package io.github.nguyennhatquang.fashion.Identity.domain.handler;

import org.springframework.http.HttpHeaders;
import java.util.Map;

import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.nguyennhatquang.fashion.Identity.delivery.dto.login.LoginRequest;
import io.github.nguyennhatquang.fashion.Identity.usecase.LoginUseCase;
import io.github.nguyennhatquang.fashion.common.infrastructure.auth.AuthResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class LoginHandler {
    private final LoginUseCase loginUseCase;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Validated @RequestBody LoginRequest.Command request) {

        // 1. Gọi UseCase để lấy AuthResponse từ Keycloak (Dùng cho cả Local & Social)
        AuthResponse authResult = loginUseCase.execute(request);

        // 2. Tạo HttpOnly Cookie cho Access Token
        ResponseCookie jwtCookie = ResponseCookie.from("access_token", authResult.accessToken())
                .httpOnly(true)
                .secure(false) // BẬT THÀNH TRUE KHI LÊN PRODUCTION (Bắt buộc HTTPS)
                .path("/") // Áp dụng cho toàn bộ domain
                .maxAge(authResult.expiresIn()) // Cookie tự hủy khi Token hết hạn
                .sameSite("Lax") // Chống tấn công CSRF
                .build();

        // 3. Tạo HttpOnly Cookie cho Refresh Token
        // ĐIỂM CHUẨN KIẾN TRÚC: Path chỉ trỏ đến đúng API refresh,
        // trình duyệt sẽ KHÔNG gửi refresh_token bừa bãi ở các API khác (như giỏ hàng,
        // profile)
        ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", authResult.refreshToken())
                .httpOnly(true)
                .secure(false) // BẬT TRUE TRÊN PROD
                .path("/api/v1/auth/refresh-token")
                .maxAge(authResult.refreshExpiresIn())
                .sameSite("Lax")
                .build();

        // 4. Trả về Response cho Frontend (Chỉ trả về thông tin vô hại)
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(Map.of(
                        "message", "Đăng nhập thành công",
                        "expires_in", authResult.expiresIn()));
    }
}
