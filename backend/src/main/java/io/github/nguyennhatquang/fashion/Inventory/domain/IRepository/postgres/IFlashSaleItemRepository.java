package io.github.nguyennhatquang.fashion.Inventory.domain.IRepository.postgres;

import io.github.nguyennhatquang.fashion.Inventory.domain.entity.FlashSaleItem;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IFlashSaleItemRepository {
    FlashSaleItem save(FlashSaleItem entity);
    List<FlashSaleItem> saveAll(List<FlashSaleItem> entities);
    FlashSaleItem update(FlashSaleItem entity);
    List<FlashSaleItem> updateAll(List<FlashSaleItem> entities);
    void delete(FlashSaleItem entity);
    void deleteById(UUID id);
    void deleteAll(List<FlashSaleItem> entities);
    void softDeleteById(UUID id);
    Optional<FlashSaleItem> findById(UUID id);
    List<FlashSaleItem> findAll();
}
