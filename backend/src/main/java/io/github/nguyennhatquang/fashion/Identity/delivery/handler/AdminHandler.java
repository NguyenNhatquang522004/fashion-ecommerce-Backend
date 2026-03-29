package io.github.nguyennhatquang.fashion.Identity.delivery.handler;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.nguyennhatquang.fashion.Identity.delivery.dto.Admin.AdminRequest;
import io.github.nguyennhatquang.fashion.Identity.domain.entity.UserProfile;
import io.github.nguyennhatquang.fashion.Identity.usecase.IUseCase.IAdminUseCase;
import io.github.nguyennhatquang.fashion.common.request.ExactPageRequest;
import io.github.nguyennhatquang.fashion.common.response.ExactPageResponse;
import io.github.nguyennhatquang.fashion.common.response.Result;
import io.github.nguyennhatquang.fashion.common.response.SystemRes;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminHandler {
    private final IAdminUseCase adminUseCase;

    @GetMapping("/get-all-user")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SystemRes> getAllUser(ExactPageRequest panigationRequest) {
        try {
            Result<ExactPageResponse<UserProfile>, Exception> result = adminUseCase.getAllUser(panigationRequest);
            if (result.hasError()) {
                return ResponseEntity.badRequest().body(
                        SystemRes.builder().status("400").message(result.error().getMessage()).data(null).build());
            }
            return ResponseEntity.ok().body(
                    SystemRes.builder().status("200").message("Get all user success").data(result.data()).build());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(
                    SystemRes.builder().status("500").message(e.getMessage()).data(null).build());
        }
    }

    @PostMapping("/created-user")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SystemRes> createdUser(@Validated @RequestBody AdminRequest.CreatedUsers createdUsers) {
        try {
            Result<UserProfile, Exception> result = adminUseCase.CreatedUser(createdUsers);
            if (result.hasError()) {
                return ResponseEntity.badRequest().body(
                        SystemRes.builder().status("400").message(result.error().getMessage()).data(null).build());
            }
            return ResponseEntity.ok().body(
                    SystemRes.builder().status("200").message("Created user success").data(result.data()).build());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(
                    SystemRes.builder().status("500").message(e.getMessage()).data(null).build());
        }
    }

    @PostMapping("/updated-user")
    public ResponseEntity<SystemRes> postMethodName(@Validated @RequestBody AdminRequest.UpdatedUsers entity) {
        try {
            Result<UserProfile, Exception> user = adminUseCase.UpdatedUser(entity);
            if (user.hasError()) {
                return ResponseEntity.badRequest().body(
                        SystemRes.builder().status("400").message(user.error().getMessage()).data(null).build());
            }

            return ResponseEntity.ok().body(
                    SystemRes.builder().status("200").message("Updated user success").data(user.data()).build());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(
                    SystemRes.builder().status("500").message(e.getMessage()).data(null).build());
        }

    }

    @PostMapping("/deleted-user")
    public ResponseEntity<SystemRes> deletedUser(@Validated @RequestBody AdminRequest.DeleteUser entity) {
        try {
            Result<UserProfile, Exception> user = adminUseCase.DeletedUser(entity);
            if (user.hasError()) {
                return ResponseEntity.badRequest().body(
                        SystemRes.builder().status("400").message(user.error().getMessage()).data(null).build());
            }
            return ResponseEntity.ok().body(
                    SystemRes.builder().status("200").message("Deleted user success").data(user.data()).build());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(
                    SystemRes.builder().status("500").message(e.getMessage()).data(null).build());
        }

    }

}
