package io.github.nguyennhatquang.fashion.Identity.usecase;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import io.github.nguyennhatquang.fashion.Identity.domain.entity.UserProfile;
import io.github.nguyennhatquang.fashion.common.Enum.TypeLoginEnum;
import io.github.nguyennhatquang.fashion.common.response.Result;

public interface ISocialUpdatedprofileUseCase {
    Result<UserProfile, Exception> execute(@AuthenticationPrincipal Jwt jwt, TypeLoginEnum typeLogin);
}
