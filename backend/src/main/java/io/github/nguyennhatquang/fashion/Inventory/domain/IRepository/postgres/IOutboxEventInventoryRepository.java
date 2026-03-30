package io.github.nguyennhatquang.fashion.Inventory.domain.IRepository.postgres;

import io.github.nguyennhatquang.fashion.Inventory.domain.entity.OutboxEventInventory;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IOutboxEventInventoryRepository {
    OutboxEventInventory save(OutboxEventInventory entity);
    List<OutboxEventInventory> saveAll(List<OutboxEventInventory> entities);
    OutboxEventInventory update(OutboxEventInventory entity);
    List<OutboxEventInventory> updateAll(List<OutboxEventInventory> entities);
    void delete(OutboxEventInventory entity);
    void deleteById(UUID id);
    void deleteAll(List<OutboxEventInventory> entities);
    void softDeleteById(UUID id);
    Optional<OutboxEventInventory> findById(UUID id);
    List<OutboxEventInventory> findAll();
}
