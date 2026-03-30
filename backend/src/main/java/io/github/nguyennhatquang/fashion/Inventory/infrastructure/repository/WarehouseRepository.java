package io.github.nguyennhatquang.fashion.Inventory.infrastructure.repository;

import io.github.nguyennhatquang.fashion.Inventory.domain.entity.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, UUID> {
}
