package io.github.nguyennhatquang.fashion.common.infrastructure.auth;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.github.nguyennhatquang.fashion.common.Enum.RoleTypeEnum;
import io.github.nguyennhatquang.fashion.common.shared.IKeycloak;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class KeycloakAdapter implements IKeycloak {
    private final Keycloak keycloak;

    @Value("${keycloak.admin.realm}")
    private String realm;

    private RealmResource getRealmResource() {
        return keycloak.realm(realm);
    }

    private UsersResource getUsersResource() {
        return getRealmResource().users();
    }

    @Override
    public UserCreatedResult createUser(UserRegistrationCmd command) {
        UserRepresentation user = new UserRepresentation();
        user.setUsername(command.username());
        user.setEmail(command.email());
        user.setFirstName(command.firstName());
        user.setLastName(command.lastName());
        user.setEnabled(true);
        user.setEmailVerified(true);

        // Thiết lập password
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(command.password());
        credential.setTemporary(false);
        user.setCredentials(Collections.singletonList(credential));

        // Call API tạo user
        try (Response response = getUsersResource().create(user)) {
            if (response.getStatus() == 201) {
                // Lấy User ID sinh ra từ Header Location của Response
                if (response.getLocation() == null || response.getLocation().getPath() == null) {
                    log.error("User created but response Location header is missing or invalid");
                    throw new RuntimeException("Failed to retrieve user ID from identity provider response");
                }
                String path = response.getLocation().getPath();
                String generatedKeycloakId = path.substring(path.lastIndexOf('/') + 1);

                // TRẢ RA DTO CHỨA ĐẦY ĐỦ THÔNG TIN
                return new UserCreatedResult(
                        generatedKeycloakId,
                        command.username(),
                        command.email());

            } else if (response.getStatus() == 409) {
                throw new RuntimeException("User with username or email already exists");
            } else {
                log.error("Failed to create user in Keycloak, status: {}, reason: {}",
                        response.getStatus(), response.getStatusInfo().getReasonPhrase());
                throw new RuntimeException("Failed to create user in identity provider");
            }
        }
    }

    private IKeycloak.UserRepresentationDto toDto(UserRepresentation kcUser) {
        if (kcUser == null)
            return null;
        return new IKeycloak.UserRepresentationDto(
                kcUser.getId(),
                kcUser.getUsername(),
                kcUser.getEmail());
    }

    @Override
    public Optional<IKeycloak.UserRepresentationDto> findByUsername(String username) {
        List<UserRepresentation> users = getUsersResource().searchByUsername(username,
                true);
        if (users.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(toDto(users.get(0)));
    }

    public List<IKeycloak.UserRepresentationDto> searchUsers(String keyword) {
        List<UserRepresentation> kcUsers = getUsersResource().search(keyword);

        // Map từng phần tử của Keycloak sang DTO nội bộ của bạn
        return kcUsers.stream()
                .map(kcUser -> new IKeycloak.UserRepresentationDto(
                        kcUser.getId(),
                        kcUser.getUsername(),
                        kcUser.getEmail()))
                .collect(Collectors.toList()); // Dùng .toList() nếu dùng Java 16+
    }

    @Override
    public void assignRole(String userId, String roleName) {
        // 1. Lấy thông tin Role từ Keycloak
        RoleRepresentation role = getRealmResource().roles().get(roleName).toRepresentation();
        if (role == null) {
            throw new IllegalArgumentException("Role " + roleName + " does not exist in Keycloak");
        }

        // 2. Map Role vào User
        UserResource userResource = getUsersResource().get(userId);
        userResource.roles().realmLevel().add(Collections.singletonList(role));
    }

    @Override
    public void deleteUser(String userId) {
        try (Response response = getUsersResource().delete(userId)) {
            if (response.getStatus() != 204) {
                log.error("Failed to delete user {}. Status: {}", userId, response.getStatus());
                throw new RuntimeException("Failed to delete user from identity provider");
            }
        }
    }

    @Override
    public void resetPassword(String userId, String newPassword) {
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(newPassword);
        credential.setTemporary(false);

        UserResource userResource = getUsersResource().get(userId);
        userResource.resetPassword(credential);
    }

    @Override
    public void getAllRoles() {
        List<RoleRepresentation> roles = getRealmResource().roles().list();
        roles.forEach(role -> log.info("Role: {}", role.getName()));
    }

    public void updateRole(String roleId, String newRoleName) {
        // 1. Lấy Resource quản lý Role theo ID (Hàm rolesById() để trống, không truyền
        // tham số)
        var roleByIdResource = getRealmResource().rolesById();

        // 2. Truyền roleId vào hàm getRole().
        // Lưu ý: Hàm này trả về thẳng RoleRepresentation chứ không cần gọi thêm
        // .toRepresentation()
        RoleRepresentation role = roleByIdResource.getRole(roleId);

        if (role == null) {
            throw new IllegalArgumentException("Role with ID " + roleId + " does not exist");
        }

        // 3. Cập nhật tên mới
        role.setName(newRoleName);

        // 4. Lưu thay đổi bằng hàm updateRole()
        roleByIdResource.updateRole(roleId, role);
    }

    @Override
    public void addRole(String roleName) {
        RoleRepresentation role = new RoleRepresentation();
        role.setName(roleName);
        getRealmResource().roles().create(role);
    }

    @Override
    public void deleteRole(String roleName) {
        getRealmResource().roles().get(roleName).remove();
    }

    @Override
    public UserRepresentation updatedOrSaveUser(UserRepresentation user) {
        if (user == null) {
            throw new IllegalArgumentException("UserRepresentation cannot be null");
        }

        String username = user.getUsername();
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username is required to update or save a user");
        }

        UsersResource usersResource = getUsersResource();
        String userId = user.getId();

        // 1. Kiểm tra user đã tồn tại chưa nếu chưa có ID
        if (userId == null) {
            // Tìm kiếm chính xác (exact match = true) theo username
            List<UserRepresentation> existingUsers = usersResource.searchByUsername(username, true);
            if (existingUsers != null && !existingUsers.isEmpty()) {
                // Lấy ID của user đang tồn tại trên hệ thống
                userId = existingUsers.get(0).getId();
                user.setId(userId); // Gắn ngược ID vào object để chuẩn bị cho bước update
            }
        }

        // 2. Thực hiện Update hoặc Create
        if (userId != null) {
            // --- LUỒNG UPDATE (Đã tồn tại) ---
            try {
                UserResource userResource = usersResource.get(userId);
                // Gọi hàm update (hàm này trả về void, không trả về Response)
                userResource.update(user);
                log.info("Successfully updated user in Keycloak with ID: {}", userId);
                if (user.getCredentials() != null && !user.getCredentials().isEmpty()) {
                    for (CredentialRepresentation cred : user.getCredentials()) {
                        if (CredentialRepresentation.PASSWORD.equals(cred.getType())) {
                            // Bắt buộc gọi resetPassword thì Keycloak mới chịu thay đổi mật khẩu
                            userResource.resetPassword(cred);
                            log.info("Successfully updated password for user: {}", username);
                        }
                    }
                }
                // Best Practice: Trả về state mới nhất trực tiếp từ Keycloak
                return userResource.toRepresentation();
            } catch (jakarta.ws.rs.NotFoundException e) {
                log.error("User with ID {} not found in Keycloak for update", userId);
                throw new RuntimeException("User not found for update in identity provider", e);
            } catch (Exception e) {
                log.error("Error updating user {} in Keycloak", username, e);
                throw new RuntimeException("Failed to update user in identity provider", e);
            }
        } else {
            // --- LUỒNG CREATE (Lưu mới) ---
            // Best Practice: Dùng try-with-resources để tự động đóng Response, tránh leak
            // connection
            try (Response response = usersResource.create(user)) {
                if (response.getStatus() == 201) {
                    // 1. Trích xuất ID vừa được Keycloak sinh ra từ Header Location
                    String path = response.getLocation().getPath();
                    String generatedId = path.substring(path.lastIndexOf('/') + 1);

                    user.setId(generatedId); // Cập nhật ID vào object hiện tại
                    log.info("Successfully created user in Keycloak with ID: {}", generatedId);

                    // 2. Gán Role mặc định (Tận dụng hàm assignRole đã viết sẵn)
                    // LƯU Ý: Tên role "User" phải khớp chính xác 100% (cả chữ hoa/chữ thường) với
                    // cấu hình trong Keycloak
                    try {
                        assignRole(generatedId, RoleTypeEnum.ROLE_USER.getValue());
                        log.info("Successfully assigned default role '{}' to user ID: {}",
                                RoleTypeEnum.ROLE_USER.getValue(), generatedId);
                    } catch (Exception e) {
                        log.error("User created but failed to assign default role '{}' for user ID: {}",
                                RoleTypeEnum.ROLE_USER.getValue(),
                                generatedId, e);
                        // Best Practice: Nên throw Exception để Rollback transaction hoặc báo lỗi cho
                        // Client biết
                        // rằng user đã được tạo nhưng chưa có quyền, tránh việc user đăng nhập được
                        // nhưng không gọi API được.
                        throw new RuntimeException("User created successfully but failed to assign default role", e);
                    }

                    return user;
                } else if (response.getStatus() == 409) {
                    // Dù đã check ở trên nhưng trong môi trường multi-thread/concurrent vẫn có thể
                    // xảy ra Conflict
                    log.error("Conflict: User with username {} already exists", username);
                    throw new RuntimeException("User with username or email already exists");
                } else {
                    log.error("Failed to create user in Keycloak. Status: {}, Reason: {}",
                            response.getStatus(), response.getStatusInfo().getReasonPhrase());
                    throw new RuntimeException("Failed to create user in identity provider");
                }
            }
        }
    }

    @Override
    public void deleteUserByEmail(String email) {
        List<UserRepresentation> users = getUsersResource().searchByUsername(email, true);
        if (users != null && !users.isEmpty()) {
            String userId = users.get(0).getId();
            deleteUser(userId);
        }
    }

    @Override
    public Optional<UserRepresentation> findByKeyEmail(String email) {
        List<UserRepresentation> users = getUsersResource().searchByUsername(email, true);
        if (users != null && !users.isEmpty()) {
            return Optional.of(users.get(0));
        }
        return Optional.empty();
    }
}
