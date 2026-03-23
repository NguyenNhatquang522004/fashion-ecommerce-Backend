package io.github.nguyennhatquang.fashion.Identity.delivery.dto.register;

import io.github.nguyennhatquang.fashion.common.Enum.GenderEnum;
import jakarta.validation.constraints.*;
import lombok.experimental.UtilityClass;

@UtilityClass
public class RegisterRequest {

    // STEP 1: Email Validation
    public record RegisterStepOne(
        @NotBlank(message = "Email không được để trống")
        @Email(message = "Định dạng email không hợp lệ", regexp = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$")
        @Size(max = 100, message = "Email không được vượt quá 100 ký tự")
        String email
    ) {
        // Compact Constructor để tự động trim dữ liệu đầu vào
        public RegisterStepOne {
            email = email != null ? email.trim().toLowerCase() : null;
        }
    }

    // STEP 2: OTP Verification
    public record RegisterStepTwo(
        @NotBlank(message = "Mã OTP không được để trống")
        @Size(min = 6, max = 6, message = "Mã OTP phải có 6 ký tự")
        String otp,
    @NotBlank(message = "Email không được để trống")
        @Email(message = "Định dạng email không hợp lệ", regexp = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$")
        @Size(max = 100, message = "Email không được vượt quá 100 ký tự")
        String email

    ) {}

    // STEP 3: Account Security
    public record RegisterStepThree(
        @NotBlank(message = "Tên đăng nhập không được để trống")
        @Size(min = 4, max = 20, message = "Tên đăng nhập phải từ 4 đến 20 ký tự")
        @Pattern(regexp = "^[a-zA-Z0-9_]*$", message = "Tên đăng nhập chỉ được chứa chữ cái, số và dấu gạch dưới")
        String username, // Sửa UserName thành username (theo chuẩn camelCase của Java)

        @NotBlank(message = "Mật khẩu không được để trống")
        @Size(min = 8, max = 50, message = "Mật khẩu phải từ 8 đến 50 ký tự")
        @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!]).*$",
            message = "Mật khẩu phải bao gồm chữ hoa, chữ thường, số và ký tự đặc biệt"
        )
        String password,

        @NotBlank(message = "Email không được để trống")
        @Email(message = "Định dạng email không hợp lệ", regexp = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$")
        @Size(max = 100, message = "Email không được vượt quá 100 ký tự")
        String email
    ) {}

    // STEP 4: Personal Information
    public record RegisterStepFour(
        @NotBlank(message = "Số điện thoại không được để trống")
        @Pattern(
            regexp = "^(0|84)(3|5|7|8|9)\\d{8}$", 
            message = "Số điện thoại Việt Nam không hợp lệ (10 số, bắt đầu bằng 0 hoặc 84)"
        )
        String phoneNumber,
        @NotBlank(message = "Địa chỉ không được để trống")
        @Size(max = 255, message = "Địa chỉ không được vượt quá 255 ký tự")
        String address,
        @NotBlank(message = "Email không được để trống")
        @Email(message = "Định dạng email không hợp lệ", regexp = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$")
        @Size(max = 100, message = "Email không được vượt quá 100 ký tự")
        String email,
        GenderEnum gender
    ) {
        public RegisterStepFour {
            phoneNumber = phoneNumber != null ? phoneNumber.trim() : null;
        }
    }
}