package io.github.nguyennhatquang.fashion.Inventory.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import io.github.nguyennhatquang.fashion.common.Enum.OutboxStatusEnum;

import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Transactional Outbox table for reliable Kafka publishing (Saga/CDC pattern).
 * A background poller reads PENDING rows and publishes them to Kafka,
 * then marks them PUBLISHED.
 *
 * Partial index for the poller:
 * CREATE INDEX idx_outbox_status ON outbox_events_inventory(status)
 * WHERE status = 'PENDING';
 * Must be created via Flyway migration (JPA @Index does not support
 * predicates).
 */
@Entity
@Table(name = "outbox_events_inventory", indexes = {
        @Index(name = "idx_outbox_status", columnList = "status"),
        @Index(name = "idx_outbox_aggregate", columnList = "aggregate_type, aggregate_id"),
        @Index(name = "idx_outbox_created_at", columnList = "created_at DESC")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
@SQLDelete(sql = "UPDATE outbox_events_inventory SET is_deleted = true WHERE id = ? AND version = ?")
@SQLRestriction("is_deleted = false")
public class OutboxEventInventory {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /** Domain aggregate this event belongs to, e.g. "INVENTORY". */
    @Column(name = "aggregate_type", length = 100, nullable = false)
    private String aggregateType;

    /**
     * ID of the InventorySummary or InventoryLedger row that triggered the event.
     */
    @Column(name = "aggregate_id", length = 100, nullable = false)
    private String aggregateId;

    /** Event type name, e.g. "StockReservedEvent", "StockReleasedEvent". */
    @Column(name = "type", length = 100, nullable = false)
    private String type;

    /**
     * Full event payload serialized as JSON.
     * Stored as PostgreSQL JSONB for efficient filtering and indexing.
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "payload", columnDefinition = "jsonb", nullable = false)
    private String payload;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(name = "status", length = 20, nullable = false)
    private OutboxStatusEnum status = OutboxStatusEnum.PENDING;

    @CreatedDate
    @Column(name = "created_at", updatable = false, nullable = false)
    private OffsetDateTime createdAt;

    /**
     * Set by the poller once the event has been successfully published to Kafka.
     */
    @Column(name = "processed_at")
    private OffsetDateTime processedAt;

    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    @CreatedBy
    @Column(name = "created_by", updatable = false)
    private String createdBy;

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
        OutboxEventInventory that = (OutboxEventInventory) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
