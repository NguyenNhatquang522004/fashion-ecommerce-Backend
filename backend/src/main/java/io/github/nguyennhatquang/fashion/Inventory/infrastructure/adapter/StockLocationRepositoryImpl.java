package io.github.nguyennhatquang.fashion.Inventory.infrastructure.adapter;

import io.github.nguyennhatquang.fashion.Inventory.domain.IRepository.postgres.IStockLocationRepository;
import io.github.nguyennhatquang.fashion.Inventory.domain.entity.StockLocation;
import io.github.nguyennhatquang.fashion.Inventory.infrastructure.repository.StockLocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class StockLocationRepositoryImpl implements IStockLocationRepository {

    private final StockLocationRepository repository;

    @Override
    public StockLocation save(StockLocation entity) {
        return repository.save(entity);
    }

    @Override
    public List<StockLocation> saveAll(List<StockLocation> entities) {
        return repository.saveAll(entities);
    }

    @Override
    public StockLocation update(StockLocation entity) {
        return repository.save(entity);
    }

    @Override
    public List<StockLocation> updateAll(List<StockLocation> entities) {
        return repository.saveAll(entities);
    }

    @Override
    public void delete(StockLocation entity) {
        repository.delete(entity);
    }

    @Override
    public void deleteById(UUID id) {
        repository.deleteById(id);
    }

    @Override
    public void deleteAll(List<StockLocation> entities) {
        repository.deleteAll(entities);
    }

    @Override
    public void softDeleteById(UUID id) {
        repository.deleteById(id); // Entity has @SQLDelete, so calling deleteById will act as soft delete
    }

    @Override
    public Optional<StockLocation> findById(UUID id) {
        return repository.findById(id);
    }

    @Override
    public List<StockLocation> findAll() {
        return repository.findAll();
    }
}
