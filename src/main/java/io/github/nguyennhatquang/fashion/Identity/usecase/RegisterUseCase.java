package io.github.nguyennhatquang.fashion.Identity.usecase;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import io.github.nguyennhatquang.fashion.Identity.delivery.dto.register.RegisterRequest;
import io.github.nguyennhatquang.fashion.Identity.domain.IRepository.postgres.IRepositoryUserProfile;
import io.github.nguyennhatquang.fashion.Identity.domain.entity.UserProfile;
import io.github.nguyennhatquang.fashion.common.Enum.TypeLoginEnum;
import io.github.nguyennhatquang.fashion.common.mail.EmailMessage;
import io.github.nguyennhatquang.fashion.common.response.Result;
import io.github.nguyennhatquang.fashion.common.shared.IEmail;
import io.github.nguyennhatquang.fashion.common.shared.IKeycloak;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RegisterUseCase implements IRegisterUseCase {
    private final IRepositoryUserProfile userProfileRepo;
    private final IKeycloak keycloakRepo;
    private final IEmail emailService;

    @Override
    public Result<UserProfile, Exception> RegisterStepOne(RegisterRequest.RegisterStepOne request) {
        // TODO Auto-generated method stub
        UserRepresentation keycloakUser = new UserRepresentation();
        keycloakUser.setEmail(request.email());
        keycloakUser.setEnabled(true);
        keycloakUser.setEmailVerified(false);
        UserRepresentation resutltkeycloak = keycloakRepo.updatedOrSaveUser(keycloakUser);
        if (resutltkeycloak == null) {
            return Result.error(new Exception("Failed to create user"));
        }
        String codeotp = String.format("%06d", new java.security.SecureRandom().nextInt(1000000));
        UserProfile user = UserProfile.builder()
                .email(request.email())
                .otp(codeotp)
                .otpExpiresAt(LocalDateTime.now().plusMinutes(10))
                .typeLogin(TypeLoginEnum.Local)
                .keycloakId(resutltkeycloak.getId())
                .build();
        userProfileRepo.save(user);
        if (user == null) {
            return Result.error(new Exception("Failed to create user"));
        }
        String html = htmlOTp(user.getEmail(), user.getOtp(), 10);
        EmailMessage message = EmailMessage.ofHtml(html, user.getEmail(), "Verify OTP");
        emailService.send(message);
        return Result.success(user);

    }

    @Override
    public Result<UserProfile, Exception> RegisterStepTwo(RegisterRequest.RegisterStepTwo request) {
        // TODO Auto-generated method stub
        UserProfile user = userProfileRepo.findbyEmail(request.email());
        if (user == null) {
            return Result.error(new Exception("User not found"));
        }
        boolean checktimeopt = user.getOtpExpiresAt().isAfter(LocalDateTime.now());

        if (!user.getOtp().equals(request.otp()) || !checktimeopt) {
            userProfileRepo.deleteByEmail(request.email());
            keycloakRepo.deleteUserByEmail(request.email());
            return Result.error(new Exception("Invalid OTP"));
        }
        user.setOtp(null);
        user.setOtpExpiresAt(null);
        userProfileRepo.save(user);
        Optional<UserRepresentation> userkeycloak = keycloakRepo.findByKeyEmail(request.email());
        if (userkeycloak.isPresent()) {
            userkeycloak.get().setEmailVerified(true);
            keycloakRepo.updatedOrSaveUser(userkeycloak.get());
        }

        return Result.success(user);
    }

    @Override
    public Result<UserProfile, Exception> RegisterStepThree(RegisterRequest.RegisterStepThree request) {
        // TODO Auto-generated method stub
        UserProfile user = userProfileRepo.findbyEmail(request.email());
        if (user == null) {
            return Result.error(new Exception("User not found"));
        }
        user.setFullName(request.username());
        userProfileRepo.save(user);
        Optional<UserRepresentation> userkeycloak = keycloakRepo.findByKeyEmail(request.email());
        if (userkeycloak.isPresent()) {
            userkeycloak.get().setUsername(request.username());

            keycloakRepo.updatedOrSaveUser(userkeycloak.get());
        }

        CredentialRepresentation resetPassword = new CredentialRepresentation();
        resetPassword.setType(CredentialRepresentation.PASSWORD);
        resetPassword.setValue(request.password());
        resetPassword.setTemporary(false);
        userkeycloak.get().setCredentials(Collections.singletonList(resetPassword));
        UserRepresentation userkeycloakv2 = keycloakRepo.updatedOrSaveUser(userkeycloak.get());
        if (userkeycloakv2 == null) {
            return Result.error(new Exception("Failed to update user"));
        }
        return Result.success(user);
    }

    @Override
    public Result<UserProfile, Exception> RegisterStepFour(RegisterRequest.RegisterStepFour request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'RegisterStepFour'");
    }

    public String htmlOTp(String name, String otpCode, int expirationMinutes) {
        return """
                <!DOCTYPE html>
                <html lang="vi">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                </head>
                <body style="font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #f4f7f6; margin: 0; padding: 40px 0;">
                    <div style="max-width: 600px; margin: 0 auto; background-color: #ffffff; border-radius: 10px; box-shadow: 0 4px 10px rgba(0,0,0,0.1); overflow: hidden;">

                        <div style="background-color: #0046c0; color: #ffffff; padding: 20px; text-align: center;">
                            <h2 style="margin: 0; font-size: 24px;">Xác thực tài khoản</h2>
                        </div>

                        <div style="padding: 30px; color: #333333; line-height: 1.6;">
                            <p style="font-size: 16px;">Xin chào <strong>%s</strong>,</p>
                            <p style="font-size: 16px;">Bạn đang thực hiện đăng ký tài khoản. Vui lòng sử dụng mã xác thực (OTP) dưới đây để hoàn tất quá trình:</p>

                            <div style="text-align: center; margin: 30px 0;">
                                <span style="display: inline-block; font-size: 36px; font-weight: bold; color: #0046c0; background-color: #f0f4ff; padding: 15px 30px; border-radius: 8px; letter-spacing: 5px; border: 1px dashed #0046c0;">
                                    %s
                                </span>
                            </div>

                            <p style="font-size: 16px; text-align: center;">
                                Mã OTP này có hiệu lực trong vòng <strong style="color: #e53935;">%d phút</strong>.
                            </p>

                            <p style="font-size: 14px; color: #666666; margin-top: 30px; padding-top: 20px; border-top: 1px solid #eeeeee;">
                                <em>Lưu ý: Nếu bạn không thực hiện yêu cầu này, vui lòng bỏ qua email. Tuyệt đối <strong>không chia sẻ</strong> mã này cho bất kỳ ai để bảo vệ tài khoản của bạn.</em>
                            </p>
                        </div>

                        <div style="background-color: #f9f9f9; padding: 15px; text-align: center; color: #888888; font-size: 12px;">
                            &copy; 2026 E-commerce Team. All rights reserved.
                        </div>
                    </div>
                </body>
                </html>
                """;
    }

}
