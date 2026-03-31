package io.github.nguyennhatquang.fashion.Order.infrastructure.Repository;

import io.github.nguyennhatquang.fashion.Order.domain.entity.PaymentTransaction;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, UUID> {

    @Query("SELECT COUNT(u.id) FROM PaymentTransaction u WHERE u.createdAt <= :snapshotTime")
    long countBySnapshot(@Param("snapshotTime") Instant snapshotTime);

    @Query("SELECT u.id FROM PaymentTransaction u WHERE u.createdAt <= :snapshotTime ORDER BY u.createdAt DESC, u.id DESC")
    List<UUID> findIdsBySnapshot(
            @Param("snapshotTime") Instant snapshotTime,
            Pageable pageable);

    @Query("SELECT u FROM PaymentTransaction u WHERE u.id IN :ids ORDER BY u.createdAt DESC, u.id DESC")
    List<PaymentTransaction> fetchFullDataByIds(@Param("ids") List<UUID> ids);
}
