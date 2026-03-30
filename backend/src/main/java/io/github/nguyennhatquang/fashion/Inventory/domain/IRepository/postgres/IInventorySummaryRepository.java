package io.github.nguyennhatquang.fashion.Inventory.domain.IRepository.postgres;

import io.github.nguyennhatquang.fashion.Inventory.domain.entity.InventorySummary;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IInventorySummaryRepository {
    InventorySummary save(InventorySummary entity);
    List<InventorySummary> saveAll(List<InventorySummary> entities);
    InventorySummary update(InventorySummary entity);
    List<InventorySummary> updateAll(List<InventorySummary> entities);
    void delete(InventorySummary entity);
    void deleteById(UUID id);
    void deleteAll(List<InventorySummary> entities);
    void softDeleteById(UUID id);
    Optional<InventorySummary> findById(UUID id);
    List<InventorySummary> findAll();
}
