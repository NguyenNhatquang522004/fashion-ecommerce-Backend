package io.github.nguyennhatquang.fashion.Identity.delivery.handler;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.nguyennhatquang.fashion.Identity.delivery.dto.UpdatedUserProfile.UpdateProfileRequest;
import io.github.nguyennhatquang.fashion.Identity.domain.entity.UserProfile;
import io.github.nguyennhatquang.fashion.Identity.usecase.IUseCase.IUpdatedUserProfileUseCase;
import io.github.nguyennhatquang.fashion.common.response.Result;
import io.github.nguyennhatquang.fashion.common.response.SystemRes;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserProfileHandler {
    private final IUpdatedUserProfileUseCase updatedUserProfileUseCase;

    @PostMapping("/update")
    public ResponseEntity<SystemRes> UpdateUserProfile(
            @Validated @RequestBody UpdateProfileRequest request) {
        try {
            Result<UserProfile, Exception> result = updatedUserProfileUseCase.updateProfile(request.getFullName(),request.getEmail(),
            request.getPhoneNumber(),request.getDob(),request.getGender(),request.getAvatarFile());
            if (result.hasError()) {
                return ResponseEntity.badRequest().body(
                        SystemRes.builder().status("400").message(result.error().getMessage()).build());
            }
            return ResponseEntity.ok().body(
                    SystemRes.builder().status("200").message("Update user profile success").build());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(
                    SystemRes.builder().status("500").message(e.getMessage()).build());
        }
    }
}
