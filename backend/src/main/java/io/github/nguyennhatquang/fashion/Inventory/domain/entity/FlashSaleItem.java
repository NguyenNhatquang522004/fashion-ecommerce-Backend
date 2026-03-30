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

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * One SKU line within a FlashSaleCampaign.
 * DB CHECK constraint guarantees total_quota > 0.
 * Cascade delete from parent campaign via ON DELETE CASCADE.
 */
@Entity
@Check(constraints = "total_quota > 0")
@Table(name = "flash_sale_items", indexes = {
        @Index(name = "idx_flash_sale_item_campaign", columnList = "campaign_id"),
        @Index(name = "idx_flash_sale_item_sku", columnList = "sku_code"),
        @Index(name = "idx_flash_sale_item_created_at_id", columnList = "created_at DESC, id DESC"),
}, uniqueConstraints = {
        @UniqueConstraint(name = "uk_flash_sale_item_campaign_sku", columnNames = { "campaign_id", "sku_code" })
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
@SQLDelete(sql = "UPDATE flash_sale_items SET is_deleted = true WHERE id = ? AND version = ?")
@SQLRestriction("is_deleted = false")
public class FlashSaleItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "campaign_id", nullable = false, foreignKey = @ForeignKey(name = "fk_flash_sale_item_campaign"))
    private FlashSaleCampaign campaign;

    /** Refers to sku_code in MongoDB sku_variants. */
    @Column(name = "sku_code", length = 100, nullable = false)
    private String skuCode;

    @Column(name = "promotional_price", nullable = false, precision = 15, scale = 2)
    private BigDecimal promotionalPrice;

    /** Total units allocated to this flash-sale slot. */
    @Column(name = "total_quota", nullable = false)
    private Integer totalQuota;

    /** Maximum units one customer is allowed to purchase. */
    @Builder.Default
    @Column(name = "purchase_limit_per_user", nullable = false)
    private Integer purchaseLimitPerUser = 1;

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
        FlashSaleItem that = (FlashSaleItem) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
