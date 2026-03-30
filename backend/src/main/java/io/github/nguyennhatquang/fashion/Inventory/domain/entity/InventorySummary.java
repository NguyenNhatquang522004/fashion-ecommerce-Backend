package io.github.nguyennhatquang.fashion.Inventory.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.generator.EventType;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Snapshot of current stock levels for one SKU at one warehouse.
 * Uses Optimistic Locking (@Version) to prevent concurrent update anomalies.
 * The `available` field is a PostgreSQL GENERATED ALWAYS AS (on_hand -
 * reserved) STORED column.
 */
@Entity
@Table(name = "inventory_summaries", indexes = {
        @Index(name = "idx_inventory_sku", columnList = "sku_code"),
        @Index(name = "idx_inventory_warehouse_id", columnList = "warehouse_id"),
        @Index(name = "idx_inventory_warehouse_sku", columnList = "warehouse_id, sku_code")
}, uniqueConstraints = {
        @UniqueConstraint(name = "uk_inventory_warehouse_sku", columnNames = { "warehouse_id", "sku_code" })
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
@SQLDelete(sql = "UPDATE inventory_summaries SET is_deleted = true WHERE id = ? AND version = ?")
@SQLRestriction("is_deleted = false")
public class InventorySummary {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "warehouse_id", nullable = false, foreignKey = @ForeignKey(name = "fk_inventory_summary_warehouse"))
    private Warehouse warehouse;

    /** Maps to sku_code in MongoDB sku_variants collection. */
    @Column(name = "sku_code", length = 100, nullable = false)
    private String skuCode;

    /** Physical stock count on hand. */
    @Builder.Default
    @Column(name = "on_hand", nullable = false)
    private Integer onHand = 0;

    /** Quantity currently held by pending orders (not yet confirmed). */
    @Builder.Default
    @Column(name = "reserved", nullable = false)
    private Integer reserved = 0;

    /**
     * Sellable stock = on_hand - reserved.
     * Computed by PostgreSQL: GENERATED ALWAYS AS (on_hand - reserved) STORED.
     * Hibernate re-reads this column after every INSERT/UPDATE.
     */
    @Generated(event = { EventType.INSERT, EventType.UPDATE })
    @Column(name = "available", insertable = false, updatable = false)
    private Integer available;

    /** Optimistic locking — Hibernate increments on every UPDATE. */
    @Version
    @Column(name = "version", nullable = false)
    private Integer version;

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
        InventorySummary that = (InventorySummary) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
