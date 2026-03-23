package io.github.nguyennhatquang.fashion.common.infrastructure.auth;

// --- 1. Java Standard Imports ---
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

// --- 2. JAX-RS (Dùng cho Response của Keycloak Admin) ---
import jakarta.ws.rs.core.Response;

// --- 3. Spring Framework Imports (CHUẨN XÁC) ---
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders; // Sửa lại thành của Spring
import org.springframework.http.MediaType; // Thêm mới
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap; // Sửa lại thành của Spring
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

// --- 4. Keycloak Admin Client Imports ---
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

// --- 5. Project Internal Imports (Thay đổi đường dẫn AuthResponse nếu cần) ---
import io.github.nguyennhatquang.fashion.common.Enum.RoleTypeEnum;
import io.github.nguyennhatquang.fashion.common.shared.IKeycloak;
// CHÚ Ý: Đảm bảo bạn đã import record AuthResponse (Tùy thuộc vào nơi bạn lưu class này)

// --- 6. Lombok ---
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class KeycloakAdapter implements IKeycloak {
    private final Keycloak keycloak;
    @Value("${keycloak.auth-server-url}") // URL server Keycloak của bạn
    private String authServerUrl;

    @Value("${keycloak.client-id}") // Tên client bạn tạo trong Keycloak (VD: fashion-app)
    private String clientId;

    @Value("${keycloak.client-secret}") // Nếu Client Access Type là Confidential
    private String clientSecret;

    // Inject RestTemplate (Bạn nhớ tạo Bean RestTemplate ở class Config nhé)
    private final RestTemplate restTemplate;

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

    @Override
    public AuthResponse loginWithPassword(String email, String password) {
        String tokenUrl = authServerUrl + "/realms/" + realm + "/protocol/openid-connect/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "password");
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("username", email);
        body.add("password", password);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<AuthResponse> response = restTemplate.postForEntity(tokenUrl, request, AuthResponse.class);
            log.info("Local login successful for user: {}", email);
            return response.getBody();
        } catch (HttpClientErrorException e) {
            log.error("Local login failed for user {}. Response: {}", email, e.getResponseBodyAsString());
            // Trả về lỗi thân thiện cho Frontend
            throw new RuntimeException("Tài khoản hoặc mật khẩu không chính xác");
        } catch (Exception e) {
            log.error("Error connecting to Keycloak", e);
            throw new RuntimeException("Hệ thống đăng nhập đang bảo trì");
        }
    }

    @Override
    public AuthResponse exchangeSocialToken(String providerAlias, String providerToken) {
        String tokenUrl = authServerUrl + "/realms/" + realm + "/protocol/openid-connect/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        // Định nghĩa đây là luồng Đổi Token
        body.add("grant_type", "urn:ietf:params:oauth:grant-type:token-exchange");
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);

        // Token mà Mobile lấy được từ Google/Facebook
        body.add("subject_token", providerToken);
        body.add("subject_token_type", "urn:ietf:params:oauth:token-type:access_token");

        // Nguồn cung cấp: "google" hoặc "facebook" (Phải cấu hình đúng Alias trong
        // Keycloak)
        body.add("subject_issuer", providerAlias);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity(body, headers);

        try {
            ResponseEntity<AuthResponse> response = restTemplate.postForEntity(tokenUrl, request, AuthResponse.class);
            log.info("Social login exchange successful for provider: {}", providerAlias);
            return response.getBody();
        } catch (HttpClientErrorException e) {
            log.error("Token exchange failed for provider {}. Response: {}", providerAlias,
                    e.getResponseBodyAsString());
            throw new RuntimeException("Xác thực qua " + providerAlias + " thất bại hoặc Token đã hết hạn.");
        }
    }

    @Override
    public void logout(String refreshToken) {
        String logoutUrl = authServerUrl + "/realms/" + realm + "/protocol/openid-connect/logout";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        // Bắt buộc phải có Refresh Token để Keycloak biết cần hủy Session nào
        body.add("refresh_token", refreshToken);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        try {
            // API logout của Keycloak trả về 204 No Content nếu thành công
            restTemplate.postForEntity(logoutUrl, request, String.class);
            log.info("Successfully logged out session from Keycloak");
        } catch (HttpClientErrorException e) {
            log.error("Failed to logout from Keycloak. Response: {}", e.getResponseBodyAsString());
            // Trả về lỗi nếu token đã hết hạn hoặc không hợp lệ
            throw new RuntimeException("Đăng xuất thất bại hoặc phiên đăng nhập đã kết thúc trước đó.");
        } catch (Exception e) {
            log.error("System error connecting to Keycloak during logout", e);
            throw new RuntimeException("Hệ thống đang bảo trì, không thể xử lý đăng xuất.");
        }
    }

    @Override
    public void sendResetPasswordEmail(String userId) {
        try {
            UserResource userResource = getUsersResource().get(userId);

            // UPDATE_PASSWORD: Hành động bắt buộc user phải đổi pass khi click link
            List<String> actions = List.of("UPDATE_PASSWORD");

            // Gửi email với các tham số:
            // - actions: danh sách hành động (Update Password)
            // - redirectUri: sau khi đổi xong thì quay về đâu (trang Login của bạn)
            // - lifespan: Link có hiệu lực trong bao lâu (giây)
            userResource.executeActionsEmail(actions);

            log.info("Successfully sent reset password email to user ID: {}", userId);
        } catch (Exception e) {
            log.error("Failed to send reset password email", e);
            throw new RuntimeException("Không thể gửi email khôi phục mật khẩu lúc này.");
        }
    }

    @Override
    public AuthResponse refreshAccessToken(String refreshToken) {
        String tokenUrl = authServerUrl + "/realms/" + realm + "/protocol/openid-connect/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "refresh_token");
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("refresh_token", refreshToken);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<AuthResponse> response = restTemplate.postForEntity(tokenUrl, request, AuthResponse.class);
            log.info("Token refreshed successfully");
            return response.getBody();
        } catch (HttpClientErrorException e) {
            log.error("Refresh token expired or invalid: {}", e.getResponseBodyAsString());
            // 100% Best Practice: Ném lỗi cụ thể để Controller biết đường xóa Cookie
            throw new RuntimeException("Refresh token expired");
        }
    }
}
