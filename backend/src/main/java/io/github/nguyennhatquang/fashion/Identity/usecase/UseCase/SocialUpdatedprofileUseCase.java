package io.github.nguyennhatquang.fashion.Identity.usecase.UseCase;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import io.github.nguyennhatquang.fashion.Identity.domain.IRepository.postgres.IRepositoryUserProfile;
import io.github.nguyennhatquang.fashion.Identity.domain.entity.UserProfile;
import io.github.nguyennhatquang.fashion.Identity.usecase.IUseCase.ISocialUpdatedprofileUseCase;
import io.github.nguyennhatquang.fashion.common.Enum.LoyaltyTierEnum;
import io.github.nguyennhatquang.fashion.common.Enum.ProfileStatusEnum;
import io.github.nguyennhatquang.fashion.common.Enum.TypeLoginEnum;
import io.github.nguyennhatquang.fashion.common.response.Result;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SocialUpdatedprofileUseCase implements ISocialUpdatedprofileUseCase {
    private final IRepositoryUserProfile userProfileRepo;

    @Override
    public Result<UserProfile, Exception> execute(@AuthenticationPrincipal Jwt jwt, TypeLoginEnum typeLogin) {
        try {
            String keycloakId = jwt.getSubject();
            String email = jwt.getClaimAsString("email");
            String fullName = jwt.getClaimAsString("name");
            String avatarUrl = jwt.getClaimAsString("picture");

            UserProfile user = new UserProfile();
            user.setKeycloakId(keycloakId);
            user.setEmail(email);
            user.setFullName(fullName);
            user.setAvatarUrl(avatarUrl);
            user.setTypeLogin(typeLogin);
            user.setStatus(ProfileStatusEnum.ACTIVE);
            user.setLoyaltyTier(LoyaltyTierEnum.BRONZE);

            UserProfile userProfilesave = userProfileRepo.save(user);
            if (userProfilesave == null) {
                return Result.error(new Exception("Failed to update user"));
            }
            return Result.success(user);
        } catch (Exception e) {
            return Result.error(e);
        }
    }
}
