package io.github.nguyennhatquang.fashion.Identity.usecase.IUseCase;

import java.time.LocalDate;

import org.springframework.web.multipart.MultipartFile;

import io.github.nguyennhatquang.fashion.Identity.domain.entity.UserProfile;
import io.github.nguyennhatquang.fashion.common.Enum.GenderEnum;
import io.github.nguyennhatquang.fashion.common.response.Result;

public interface IUpdatedUserProfileUseCase {
    Result<UserProfile, Exception> updateProfile(String fullName, String email, String phoneNumber,
            LocalDate dob,
            GenderEnum gender,
            String avatarFile);
}
