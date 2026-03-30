package io.github.nguyennhatquang.fashion.Inventory.infrastructure.adapter;

import io.github.nguyennhatquang.fashion.Inventory.domain.IRepository.postgres.IOutboxEventInventoryRepository;
import io.github.nguyennhatquang.fashion.Inventory.domain.entity.OutboxEventInventory;
import io.github.nguyennhatquang.fashion.Inventory.infrastructure.repository.OutboxEventInventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class OutboxEventInventoryRepositoryImpl implements IOutboxEventInventoryRepository {

    private final OutboxEventInventoryRepository repository;

    @Override
    public OutboxEventInventory save(OutboxEventInventory entity) {
        return repository.save(entity);
    }

    @Override
    public List<OutboxEventInventory> saveAll(List<OutboxEventInventory> entities) {
        return repository.saveAll(entities);
    }

    @Override
    public OutboxEventInventory update(OutboxEventInventory entity) {
        return repository.save(entity);
    }

    @Override
    public List<OutboxEventInventory> updateAll(List<OutboxEventInventory> entities) {
        return repository.saveAll(entities);
    }

    @Override
    public void delete(OutboxEventInventory entity) {
        repository.delete(entity);
    }

    @Override
    public void deleteById(UUID id) {
        repository.deleteById(id);
    }

    @Override
    public void deleteAll(List<OutboxEventInventory> entities) {
        repository.deleteAll(entities);
    }

    @Override
    public void softDeleteById(UUID id) {
        repository.deleteById(id); // Entity has @SQLDelete, so calling deleteById will act as soft delete
    }

    @Override
    public Optional<OutboxEventInventory> findById(UUID id) {
        return repository.findById(id);
    }

    @Override
    public List<OutboxEventInventory> findAll() {
        return repository.findAll();
    }
}
