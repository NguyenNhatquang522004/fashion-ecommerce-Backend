package io.github.nguyennhatquang.fashion.Inventory.infrastructure.adapter;

import io.github.nguyennhatquang.fashion.Inventory.domain.IRepository.postgres.IWarehouseRepository;
import io.github.nguyennhatquang.fashion.Inventory.domain.entity.Warehouse;
import io.github.nguyennhatquang.fashion.Inventory.infrastructure.repository.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class WarehouseRepositoryImpl implements IWarehouseRepository {

    private final WarehouseRepository repository;

    @Override
    public Warehouse save(Warehouse entity) {
        return repository.save(entity);
    }

    @Override
    public List<Warehouse> saveAll(List<Warehouse> entities) {
        return repository.saveAll(entities);
    }

    @Override
    public Warehouse update(Warehouse entity) {
        return repository.save(entity);
    }

    @Override
    public List<Warehouse> updateAll(List<Warehouse> entities) {
        return repository.saveAll(entities);
    }

    @Override
    public void delete(Warehouse entity) {
        repository.delete(entity);
    }

    @Override
    public void deleteById(UUID id) {
        repository.deleteById(id);
    }

    @Override
    public void deleteAll(List<Warehouse> entities) {
        repository.deleteAll(entities);
    }

    @Override
    public void softDeleteById(UUID id) {
        repository.deleteById(id); // Entity has @SQLDelete, so calling deleteById will act as soft delete
    }

    @Override
    public Optional<Warehouse> findById(UUID id) {
        return repository.findById(id);
    }

    @Override
    public List<Warehouse> findAll() {
        return repository.findAll();
    }
}
