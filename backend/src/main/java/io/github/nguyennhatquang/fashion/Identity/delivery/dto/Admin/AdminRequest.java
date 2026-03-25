package io.github.nguyennhatquang.fashion.Identity.delivery.dto.Admin;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import io.github.nguyennhatquang.fashion.common.Enum.GenderEnum;
import io.github.nguyennhatquang.fashion.common.Enum.RoleTypeEnum;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.experimental.UtilityClass;

@UtilityClass
public class AdminRequest {
        public record CreatedUsers(
                        @NotBlank(message = "Email is required") @Email(message = "Email is invalid") String email,
                        @NotBlank(message = "Password is required") String password,
                        @NotBlank(message = "Full name is required") String fullName,
                        @NotNull(message = "Gender is required") GenderEnum gender,
                        @NotNull(message = "Date of birth is required") LocalDate dob,
                        @NotBlank(message = "Phone number is required") String phoneNumber) {
        }

        public record UpdatedUsers(
                        @NotBlank(message = "Email is required") @Email(message = "Email is invalid") String email,
                        @NotBlank(message = "Full name is required") String fullName,
                        @NotNull(message = "Gender is required") GenderEnum gender,
                        @NotNull(message = "Date of birth is required") LocalDate dob,
                        @NotBlank(message = "Phone number is required") String phoneNumber,
                        @NotNull(message = "Roles is required") List<RoleTypeEnum> roles) {
        }

        public record DeleteUser(
                        @NotBlank(message = "Email is required") @Email(message = "Email is invalid") String email) {
        }

}
