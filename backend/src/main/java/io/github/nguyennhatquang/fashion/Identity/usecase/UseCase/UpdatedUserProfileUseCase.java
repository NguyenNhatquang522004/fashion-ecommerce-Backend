package io.github.nguyennhatquang.fashion.Identity.usecase.UseCase;

import java.time.LocalDate;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import io.github.nguyennhatquang.fashion.Identity.delivery.mapper.UserProfileMapper;
import io.github.nguyennhatquang.fashion.Identity.domain.IRepository.postgres.IRepositoryUserProfile;
import io.github.nguyennhatquang.fashion.Identity.domain.entity.UserProfile;
import io.github.nguyennhatquang.fashion.Identity.usecase.IUseCase.IUpdatedUserProfileUseCase;
import io.github.nguyennhatquang.fashion.common.Enum.GenderEnum;
import io.github.nguyennhatquang.fashion.common.response.Result;
import io.github.nguyennhatquang.fashion.common.shared.ISeaweedfs;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UpdatedUserProfileUseCase implements IUpdatedUserProfileUseCase {
    private final IRepositoryUserProfile userProfileRepo;
    private final ISeaweedfs seaweedfs;

    @Override
    public Result<UserProfile, Exception> updateProfile(String fullName, String email,
            String phoneNumber,
            LocalDate dob,
            GenderEnum gender,
            String avatarFile) {
        try {
            // 1. Lấy claim "email" từ JWT
            // 1. Tìm UserProfile
            UserProfile userProfile = userProfileRepo.findbyEmail(email);
            if (userProfile == null) {
                return Result.error(new Exception("User profile not found"));
            }
            userProfile.setFullName(fullName);
            userProfile.setPhoneNumber(phoneNumber);
            userProfile.setDob(dob);
            userProfile.setGender(gender);
            if (avatarFile != null && !avatarFile.isEmpty()) {
                if (userProfile.getAvatarUrl() != null) {
                    seaweedfs.deleteFile(userProfile.getAvatarUrl());
                }
                userProfile.setAvatarUrl(avatarFile);
            }
            // 4. Lưu lại
            UserProfile updated = userProfileRepo.save(userProfile);
            if (updated == null) {
                return Result.error(new Exception("Update user profile failed"));
            }
            return Result.success(updated);
        } catch (Exception e) {
            return Result.error(e);
        }
    }
}
