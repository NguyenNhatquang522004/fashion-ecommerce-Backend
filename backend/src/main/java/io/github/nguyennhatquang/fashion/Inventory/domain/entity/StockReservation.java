package io.github.nguyennhatquang.fashion.Inventory.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import io.github.nguyennhatquang.fashion.common.Enum.ReservationStatusEnum;

import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Temporary stock hold placed when a customer initiates checkout.
 * Expires automatically (expires_at) if payment is not completed.
 *
 * Partial index for expired-scan:
 * CREATE INDEX idx_reservation_expires ON stock_reservations(expires_at)
 * WHERE status = 'RESERVED';
 * This index must be created via a Flyway migration (not expressible in JPA).
 */
@Entity
@Table(name = "stock_reservations", indexes = {
        @Index(name = "idx_reservation_order", columnList = "order_id"),
        @Index(name = "idx_reservation_sku", columnList = "sku_code"),
        @Index(name = "idx_reservation_warehouse", columnList = "warehouse_id"),
        @Index(name = "idx_reservation_expires", columnList = "expires_at"),
        @Index(name = "idx_reservation_status_exp", columnList = "status, expires_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
@SQLDelete(sql = "UPDATE stock_reservations SET is_deleted = true WHERE id = ? AND version = ?")
@SQLRestriction("is_deleted = false")
public class StockReservation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "order_id", length = 100, nullable = false)
    private String orderId;

    @Column(name = "sku_code", length = 100, nullable = false)
    private String skuCode;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "warehouse_id", nullable = false, foreignKey = @ForeignKey(name = "fk_reservation_warehouse"))
    private Warehouse warehouse;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(name = "status", length = 50, nullable = false)
    private ReservationStatusEnum status = ReservationStatusEnum.RESERVED;

    /**
     * Wall-clock deadline; a scheduler/Kafka delay-queue transitions status →
     * EXPIRED after this.
     */
    @Column(name = "expires_at", nullable = false)
    private OffsetDateTime expiresAt;

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
        StockReservation that = (StockReservation) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
