package io.github.nguyennhatquang.fashion.Inventory.infrastructure.adapter;

import io.github.nguyennhatquang.fashion.Inventory.domain.IRepository.postgres.IFlashSaleItemRepository;
import io.github.nguyennhatquang.fashion.Inventory.domain.entity.FlashSaleItem;
import io.github.nguyennhatquang.fashion.Inventory.infrastructure.repository.FlashSaleItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class FlashSaleItemRepositoryImpl implements IFlashSaleItemRepository {

    private final FlashSaleItemRepository repository;

    @Override
    public FlashSaleItem save(FlashSaleItem entity) {
        return repository.save(entity);
    }

    @Override
    public List<FlashSaleItem> saveAll(List<FlashSaleItem> entities) {
        return repository.saveAll(entities);
    }

    @Override
    public FlashSaleItem update(FlashSaleItem entity) {
        return repository.save(entity);
    }

    @Override
    public List<FlashSaleItem> updateAll(List<FlashSaleItem> entities) {
        return repository.saveAll(entities);
    }

    @Override
    public void delete(FlashSaleItem entity) {
        repository.delete(entity);
    }

    @Override
    public void deleteById(UUID id) {
        repository.deleteById(id);
    }

    @Override
    public void deleteAll(List<FlashSaleItem> entities) {
        repository.deleteAll(entities);
    }

    @Override
    public void softDeleteById(UUID id) {
        repository.deleteById(id); // Entity has @SQLDelete, so calling deleteById will act as soft delete
    }

    @Override
    public Optional<FlashSaleItem> findById(UUID id) {
        return repository.findById(id);
    }

    @Override
    public List<FlashSaleItem> findAll() {
        return repository.findAll();
    }
}
