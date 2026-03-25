package io.github.nguyennhatquang.fashion.Identity.delivery.handler;

import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.nguyennhatquang.fashion.Identity.usecase.IUseCase.IRefreshTokenUseCase;
import io.github.nguyennhatquang.fashion.Identity.usecase.UseCase.RefreshTokenUseCase;
import io.github.nguyennhatquang.fashion.common.infrastructure.auth.AuthResponse;
import io.github.nguyennhatquang.fashion.common.response.SystemRes;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class RefreshTokenHandler {
    private final RefreshTokenUseCase refreshTokenUseCase;

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(
            @CookieValue(name = "refresh_token", required = false) String refreshTokenFromCookie) {

        try {
            // 1. Lấy token từ Cookie (Web) hoặc Body (Mobile)
            if (refreshTokenFromCookie == null || refreshTokenFromCookie.isBlank()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing refresh token");
            }

            // 2. Gọi UseCase đổi token
            AuthResponse authResult = refreshTokenUseCase.execute(refreshTokenFromCookie);

            // 3. Tạo mới Access Token Cookie
            ResponseCookie jwtCookie = ResponseCookie.from("access_token", authResult.accessToken())
                    .httpOnly(true)
                    .secure(false) // Đổi thành true trên Production (HTTPS)
                    .path("/")
                    .maxAge(authResult.expiresIn())
                    .sameSite("Lax")
                    .build();

            // 4. Tạo mới Refresh Token Cookie (Ghi đè cái cũ)
            ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", authResult.refreshToken())
                    .httpOnly(true)
                    .secure(false)
                    .path("/api/v1/auth/refresh-token") // Giới hạn đúng path bảo mật
                    .maxAge(authResult.refreshExpiresIn())
                    .sameSite("Lax")
                    .build();

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                    .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                    .body(Map.of(
                            "message", "Token refreshed",
                            "expires_in", authResult.expiresIn()));

        } catch (Exception e) {
            // 5. NẾU REFRESH FAIL (Token hết hạn thật): Xóa sạch Cookie để User phải đăng
            // nhập lại
            ResponseCookie clearJwt = ResponseCookie.from("access_token", "").path("/").maxAge(0).build();
            ResponseCookie clearRefresh = ResponseCookie.from("refresh_token", "").path("/api/v1/auth/refresh-token")
                    .maxAge(0).build();

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .header(HttpHeaders.SET_COOKIE, clearJwt.toString())
                    .header(HttpHeaders.SET_COOKIE, clearRefresh.toString())
                    .body("Session expired. Please login again.");
        }
    }
}
