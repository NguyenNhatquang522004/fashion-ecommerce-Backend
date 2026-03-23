package io.github.nguyennhatquang.fashion.Identity.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import io.github.nguyennhatquang.fashion.common.Enum.GenderEnum;
import io.github.nguyennhatquang.fashion.common.Enum.LoyaltyTierEnum;
import io.github.nguyennhatquang.fashion.common.Enum.ProfileStatusEnum;
import io.github.nguyennhatquang.fashion.common.Enum.TypeLoginEnum;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "user_profiles", indexes = {
        @Index(name = "idx_user_profiles_email", columnList = "email"),
        @Index(name = "idx_user_profiles_phone_number", columnList = "phone_number"),
        @Index(name = "idx_user_profiles_keycloak_id", columnList = "keycloak_id"),
        @Index(name = "idx_user_profile_loyalty_tier_and_status", columnList = "loyalty_tier, status")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
@SQLDelete(sql = "UPDATE user_profiles SET is_deleted = true WHERE id = ? AND version = ?")
@SQLRestriction("is_deleted = false")
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "keycloak_id", nullable = false, unique = true, updatable = false)
    private String keycloakId;

    @Column(name = "otp", nullable = false, unique = true)
    private String otp;

    @Column(name = "otp_expires_at", nullable = false)
    private LocalDateTime otpExpiresAt;

    @Column(name = "count_otp", nullable = false)
    private Integer countOtp;

    @Column(name = "time_resend_Email")
    private LocalDateTime timeResendEmail;

    @Column(name = "type_login", nullable = false)
    private TypeLoginEnum typeLogin;

    @Column(unique = true)
    private String email;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "phone_number", unique = true)
    private String phoneNumber;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Column(name = "dob")
    private LocalDate dob;

    @Enumerated(EnumType.STRING)
    @Column(length = 20) // Nên set độ dài tối đa cho Enum dưới DB
    private GenderEnum gender;

    @Enumerated(EnumType.STRING)
    @Column(name = "loyalty_tier", length = 20)
    private LoyaltyTierEnum loyaltyTier;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private ProfileStatusEnum status;

    @Version
    private Long version;

    // --- Bắt đầu phần Auditing ---
    @CreatedBy
    @Column(name = "created_by", updatable = false)
    private String createdBy;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedBy
    @Column(name = "updated_by")
    private String updatedBy;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    // --- Kết thúc phần Auditing ---

    @Builder.Default // Đảm bảo khi dùng Builder, giá trị mặc định là false
    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;

    // Tự định nghĩa equals và hashCode dựa trên Business Key (id hoặc keycloakId)
    // để tránh lỗi với @Data
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        UserProfile that = (UserProfile) o;
        return Objects.equals(id, that.id) && Objects.equals(keycloakId, that.keycloakId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, keycloakId);
    }
}