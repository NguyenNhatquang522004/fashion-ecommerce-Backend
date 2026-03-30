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

import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "stock_locations", indexes = {
        @Index(name = "idx_stock_location_warehouse_id", columnList = "warehouse_id"),
        @Index(name = "idx_stock_location_created_at_id", columnList = "created_at DESC, id DESC")
}, uniqueConstraints = {
        @UniqueConstraint(name = "uk_stock_location_position", columnNames = { "warehouse_id", "zone", "aisle", "rack",
                "shelf" })
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
@SQLDelete(sql = "UPDATE stock_locations SET is_deleted = true WHERE id = ? AND version = ?")
@SQLRestriction("is_deleted = false")
public class StockLocation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "warehouse_id", nullable = false, foreignKey = @ForeignKey(name = "fk_stock_location_warehouse"))
    private Warehouse warehouse;

    @Column(name = "zone", length = 50)
    private String zone;

    @Column(name = "aisle", length = 50)
    private String aisle;

    @Column(name = "rack", length = 50)
    private String rack;

    @Column(name = "shelf", length = 50)
    private String shelf;

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
        StockLocation that = (StockLocation) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
