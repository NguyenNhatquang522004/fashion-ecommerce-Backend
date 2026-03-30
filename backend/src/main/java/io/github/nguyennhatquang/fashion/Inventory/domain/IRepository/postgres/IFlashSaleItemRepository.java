package io.github.nguyennhatquang.fashion.Inventory.domain.IRepository.postgres;

import io.github.nguyennhatquang.fashion.Catalog.domain.entity.Product;
import io.github.nguyennhatquang.fashion.Inventory.domain.entity.FlashSaleItem;
import io.github.nguyennhatquang.fashion.common.request.ExactPageRequest;
import io.github.nguyennhatquang.fashion.common.response.ExactPageResponse;

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

    ExactPageResponse<FlashSaleItem> getFlashSaleItemExactPage(ExactPageRequest request);
}
