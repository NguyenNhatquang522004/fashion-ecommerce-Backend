package io.github.nguyennhatquang.fashion.common.shared;

import java.util.List;
import java.util.Optional;

import org.keycloak.representations.idm.UserRepresentation;

import io.github.nguyennhatquang.fashion.common.Enum.RoleTypeEnum;
import io.github.nguyennhatquang.fashion.common.infrastructure.auth.AuthResponse;

public interface IKeycloak {
    // Record DTO nội bộ để truyền dữ liệu
    record UserRegistrationCmd(String username, String email, String firstName, String lastName, String password) {
    }

    record UserRepresentationDto(String id, String username, String email) {
    }

    record UserCreatedResult(String keycloakId, String username, String email) {
    }

    /**
     * Tạo user mới và trả về User ID (UUID) được Identity Provider sinh ra.
     */
    UserCreatedResult createUser(UserRegistrationCmd command);

    /**
     * Tìm kiếm user theo Username
     */
    Optional<UserRepresentationDto> findByUsername(String username);

    Optional<UserRepresentation> findByKeyEmail(String email);

    UserRepresentation updatedOrSaveUser(UserRepresentation user);

    /**
     * Gán Role cho một User
     */
    void assignRole(String userId, String roleName);

    /**
     * Xóa User
     */
    void deleteUser(String userId);

    /**
     * Cập nhật mật khẩu
     */
    void resetPassword(String userId, String newPassword);

    void getAllRoles();

    void updateRole(String roleId, String newRoleName);

    void syncUserRoles(String userId, List<RoleTypeEnum> newRoleNames);

    void addRole(String roleName);

    void deleteRole(String roleName);

    void deleteUserByEmail(String email);

    AuthResponse loginWithPassword(String email, String password);

    AuthResponse exchangeSocialToken(String providerAlias, String providerToken);

    void logout(String refreshToken);

    void sendResetPasswordEmail(String userId);

    AuthResponse refreshAccessToken(String refreshToken);
}
