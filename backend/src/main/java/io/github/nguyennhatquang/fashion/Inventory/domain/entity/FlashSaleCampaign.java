package io.github.nguyennhatquang.fashion.Inventory.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Check;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import io.github.nguyennhatquang.fashion.common.Enum.CampaignStatusEnum;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * A time-bounded flash sale campaign.
 * DB CHECK constraint ensures end_time is always after start_time.
 */
@Entity
@Check(constraints = "end_time > start_time")
@Table(name = "flash_sale_campaigns", indexes = {
        @Index(name = "idx_campaign_status", columnList = "status"),
        @Index(name = "idx_campaign_time_range", columnList = "start_time, end_time"),
        @Index(name = "idx_campaign_created_at_id", columnList = "created_at DESC, id DESC"),
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
@SQLDelete(sql = "UPDATE flash_sale_campaigns SET is_deleted = true WHERE id = ? AND version = ?")
@SQLRestriction("is_deleted = false")
public class FlashSaleCampaign {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "name", length = 255, nullable = false)
    private String name;

    @Column(name = "start_time", nullable = false)
    private OffsetDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private OffsetDateTime endTime;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(name = "status", length = 50, nullable = false)
    private CampaignStatusEnum status = CampaignStatusEnum.DRAFT;

    @Builder.Default
    @OneToMany(mappedBy = "campaign", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<FlashSaleItem> items = new ArrayList<>();

    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    @CreatedBy
    @Column(name = "created_by", updatable = false)
    private String createdBy;

    @CreatedDate
    @Column(name = "created_at", updatable = false, nullable = false)
    private OffsetDateTime createdAt;

    @LastModifiedBy
    @Column(name = "updated_by")
    private String updatedBy;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @Builder.Default
    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        FlashSaleCampaign that = (FlashSaleCampaign) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
