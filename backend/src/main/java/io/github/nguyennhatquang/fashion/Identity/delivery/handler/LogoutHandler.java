package io.github.nguyennhatquang.fashion.Identity.delivery.handler;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.nguyennhatquang.fashion.Identity.usecase.IUseCase.Ilogout;
import io.github.nguyennhatquang.fashion.common.response.Result;
import io.github.nguyennhatquang.fashion.common.response.SystemRes;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.RequiredArgsConstructor;

@RestController // Dùng RestController thay vì @Controller cho API
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class LogoutHandler {
    private final Ilogout logoutUseCase;

    @PostMapping("/logout")
    public ResponseEntity<SystemRes> logout(
            @CookieValue(name = "refresh_token", required = true) String refreshToken) {

        // 1. Gọi UseCase xử lý logout trên Keycloak
        Result<Void, Exception> result = logoutUseCase.Logout(refreshToken);

        if (result.hasError()) {
            return ResponseEntity.badRequest().body(
                    SystemRes.builder().status("400").message(result.error().getMessage()).build());
        }

        // 2. Tạo lệnh xóa Access Token Cookie
        ResponseCookie deleteJwtCookie = ResponseCookie.from("access_token", "")
                .httpOnly(true)
                .secure(false) // Đổi thành true nếu dùng HTTPS
                .path("/")
                .maxAge(0)
                .build();

        // 3. Tạo lệnh xóa Refresh Token Cookie (Phải khớp Path)
        ResponseCookie deleteRefreshCookie = ResponseCookie.from("refresh_token", "")
                .httpOnly(true)
                .secure(false)
                .path("/api/v1/auth/refresh-token")
                .maxAge(0)
                .build();

        // 4. QUAN TRỌNG NHẤT: Đính kèm vào Header để trình duyệt thực thi xóa
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, deleteJwtCookie.toString())
                .header(HttpHeaders.SET_COOKIE, deleteRefreshCookie.toString())
                .body(SystemRes.builder().status("200").message("Logout success").build());
    }
}
