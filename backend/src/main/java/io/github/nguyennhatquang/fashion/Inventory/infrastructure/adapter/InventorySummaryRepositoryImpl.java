package io.github.nguyennhatquang.fashion.Inventory.infrastructure.adapter;

import io.github.nguyennhatquang.fashion.Inventory.domain.IRepository.postgres.IInventorySummaryRepository;
import io.github.nguyennhatquang.fashion.Inventory.domain.entity.InventorySummary;
import io.github.nguyennhatquang.fashion.Inventory.infrastructure.repository.InventorySummaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class InventorySummaryRepositoryImpl implements IInventorySummaryRepository {

    private final InventorySummaryRepository repository;

    @Override
    public InventorySummary save(InventorySummary entity) {
        return repository.save(entity);
    }

    @Override
    public List<InventorySummary> saveAll(List<InventorySummary> entities) {
        return repository.saveAll(entities);
    }

    @Override
    public InventorySummary update(InventorySummary entity) {
        return repository.save(entity);
    }

    @Override
    public List<InventorySummary> updateAll(List<InventorySummary> entities) {
        return repository.saveAll(entities);
    }

    @Override
    public void delete(InventorySummary entity) {
        repository.delete(entity);
    }

    @Override
    public void deleteById(UUID id) {
        repository.deleteById(id);
    }

    @Override
    public void deleteAll(List<InventorySummary> entities) {
        repository.deleteAll(entities);
    }

    @Override
    public void softDeleteById(UUID id) {
        repository.deleteById(id); // Entity has @SQLDelete, so calling deleteById will act as soft delete
    }

    @Override
    public Optional<InventorySummary> findById(UUID id) {
        return repository.findById(id);
    }

    @Override
    public List<InventorySummary> findAll() {
        return repository.findAll();
    }
}
