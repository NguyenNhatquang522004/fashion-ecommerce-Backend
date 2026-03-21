package io.github.nguyennhatquang.fashion.Identity.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import io.github.nguyennhatquang.fashion.common.Enum.AddressTypeEnum;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "user_addresses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
@SQLDelete(sql = "UPDATE user_addresses SET is_deleted = true WHERE id = ? AND version = ?")
@SQLRestriction("is_deleted = false")
public class UserAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_profile_id", nullable = false, updatable = false)
    private UserProfile userProfile;

    @Column(name = "receiver_name", nullable = false)
    private String receiverName;

    @Column(name = "receiver_phone", nullable = false)
    private String receiverPhone;

    @Column(name = "street_line", nullable = false)
    private String streetLine;

    // --- Địa chỉ hành chính (Dùng Code để map API Giao hàng) ---
    @Column(name = "ward_code", nullable = false, length = 20)
    private String wardCode;

    @Column(name = "ward_name", nullable = false, length = 100)
    private String wardName;

    @Column(name = "district_code", nullable = false, length = 20)
    private String districtCode;

    @Column(name = "district_name", nullable = false, length = 100)
    private String districtName;

    @Column(name = "province_code", nullable = false, length = 20)
    private String provinceCode;

    @Column(name = "province_name", nullable = false, length = 100)
    private String provinceName;

    // --- Tọa độ ---
    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    // --- Phân loại & Mặc định ---
    @Enumerated(EnumType.STRING)
    @Column(name = "address_type", length = 20)
    private AddressTypeEnum addressType;

    @Column(name = "is_default", nullable = false)
    private Boolean isDefault = false;

    @Version
    private Long version;

    // --- Auditing ---
    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;

    // --- Enums ---

}