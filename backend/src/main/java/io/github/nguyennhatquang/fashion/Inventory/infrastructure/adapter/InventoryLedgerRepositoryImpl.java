package io.github.nguyennhatquang.fashion.Inventory.infrastructure.adapter;

import io.github.nguyennhatquang.fashion.Inventory.domain.IRepository.postgres.IInventoryLedgerRepository;
import io.github.nguyennhatquang.fashion.Inventory.domain.entity.InventoryLedger;
import io.github.nguyennhatquang.fashion.Inventory.infrastructure.repository.InventoryLedgerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class InventoryLedgerRepositoryImpl implements IInventoryLedgerRepository {

    private final InventoryLedgerRepository repository;

    @Override
    public InventoryLedger save(InventoryLedger entity) {
        return repository.save(entity);
    }

    @Override
    public List<InventoryLedger> saveAll(List<InventoryLedger> entities) {
        return repository.saveAll(entities);
    }

    @Override
    public InventoryLedger update(InventoryLedger entity) {
        return repository.save(entity);
    }

    @Override
    public List<InventoryLedger> updateAll(List<InventoryLedger> entities) {
        return repository.saveAll(entities);
    }

    @Override
    public void delete(InventoryLedger entity) {
        repository.delete(entity);
    }

    @Override
    public void deleteById(UUID id) {
        repository.deleteById(id);
    }

    @Override
    public void deleteAll(List<InventoryLedger> entities) {
        repository.deleteAll(entities);
    }

    @Override
    public void softDeleteById(UUID id) {
        repository.deleteById(id); // Entity has @SQLDelete, so calling deleteById will act as soft delete
    }

    @Override
    public Optional<InventoryLedger> findById(UUID id) {
        return repository.findById(id);
    }

    @Override
    public List<InventoryLedger> findAll() {
        return repository.findAll();
    }
}
