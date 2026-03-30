package io.github.nguyennhatquang.fashion.Inventory.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import io.github.nguyennhatquang.fashion.common.Enum.InventoryTransactionTypeEnum;

import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Append-only audit ledger for every stock movement.
 * NEVER updated or deleted — financial audit requirement.
 * Hibernate @Immutable enforces this at the ORM level.
 */
@Entity
@Immutable
@Table(name = "inventory_ledger", indexes = {
        @Index(name = "idx_ledger_warehouse_id", columnList = "warehouse_id"),
        @Index(name = "idx_ledger_sku_code", columnList = "sku_code"),
        @Index(name = "idx_ledger_reference_id", columnList = "reference_id"),
        @Index(name = "idx_ledger_transaction_type", columnList = "transaction_type"),
        @Index(name = "idx_ledger_created_at", columnList = "created_at DESC")
})
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
@SQLDelete(sql = "UPDATE inventory_ledger SET is_deleted = true WHERE id = ? AND version = ?")
@SQLRestriction("is_deleted = false")
public class InventoryLedger {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "warehouse_id", nullable = false, foreignKey = @ForeignKey(name = "fk_ledger_warehouse"))
    private Warehouse warehouse;

    @Column(name = "sku_code", length = 100, nullable = false)
    private String skuCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", length = 50, nullable = false)
    private InventoryTransactionTypeEnum transactionType;

    /** Positive = stock in, Negative = stock out. */
    @Column(name = "quantity_change", nullable = false)
    private Integer quantityChange;

    /**
     * Order ID, purchase-order ID, or inbound receipt ID that triggered this entry.
     */
    @Column(name = "reference_id", length = 100)
    private String referenceId;

    @Column(name = "note", columnDefinition = "TEXT")
    private String note;

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
        InventoryLedger that = (InventoryLedger) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
