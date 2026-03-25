package io.github.nguyennhatquang.fashion.Identity.delivery.handler;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.nguyennhatquang.fashion.Identity.delivery.dto.UserAddress.UserAddressRequest;
import io.github.nguyennhatquang.fashion.Identity.delivery.dto.UserAddress.UserAddressResponse;
import io.github.nguyennhatquang.fashion.Identity.domain.entity.UserAddress;
import io.github.nguyennhatquang.fashion.Identity.usecase.IUseCase.IUserAddressUseCase;
import io.github.nguyennhatquang.fashion.common.request.PanigationRequest;
import io.github.nguyennhatquang.fashion.common.response.PanigationResponse;
import io.github.nguyennhatquang.fashion.common.response.Result;
import io.github.nguyennhatquang.fashion.common.response.SystemRes;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/user-addresses")
@RequiredArgsConstructor
public class UserAddresshandler {

    private final IUserAddressUseCase userAddressUseCase;

    @GetMapping
    public ResponseEntity<SystemRes> GetListUserAddress(PanigationRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        try {
            UUID userId = UUID.fromString(jwt.getClaimAsString("userId"));
            Result<PanigationResponse<UserAddress>, Exception> result = userAddressUseCase.GetListUserAddress(userId,
                    request);
            if (result.hasError()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(SystemRes.builder().status("400").message(result.error().getMessage()).build());
            }
            return ResponseEntity.ok().body(SystemRes.builder().status("200").message("Get list user address success")
                    .data(result.data()).build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(SystemRes.builder().status("500").message(e.getMessage()).build());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<SystemRes> GetDetailUserAddress(@PathVariable UUID id) {
        try {
            Result<UserAddressResponse, Exception> result = userAddressUseCase.GetDetailUserAddress(id);
            if (result.hasError()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(SystemRes.builder().status("400").message(result.error().getMessage()).build());
            }
            return ResponseEntity.ok().body(SystemRes.builder().status("200").message("Get detail user address success")
                    .data(result.data()).build());

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(SystemRes.builder().status("500").message(e.getMessage()).build());
        }
    }

    @PostMapping
    public ResponseEntity<SystemRes> CreateUserAddress(@RequestBody UserAddressRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        try {
            UUID userId = UUID.fromString(jwt.getClaimAsString("userId"));
            Result<UserAddress, Exception> data = userAddressUseCase.CreateUserAddress(userId, request);
            if (data.hasError()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(SystemRes.builder().status("400").message(data.error().getMessage()).build());
            }
            return ResponseEntity.ok().body(SystemRes.builder().status("200").message("Create user address success")
                    .data(data.data()).build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(SystemRes.builder().status("500").message(e.getMessage()).build());
        }
    }

}
