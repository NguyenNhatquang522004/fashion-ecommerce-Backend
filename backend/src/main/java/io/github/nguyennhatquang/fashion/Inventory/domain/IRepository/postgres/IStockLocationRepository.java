package io.github.nguyennhatquang.fashion.Inventory.domain.IRepository.postgres;

import io.github.nguyennhatquang.fashion.Catalog.domain.entity.Product;
import io.github.nguyennhatquang.fashion.Inventory.domain.entity.StockLocation;
import io.github.nguyennhatquang.fashion.common.request.ExactPageRequest;
import io.github.nguyennhatquang.fashion.common.response.ExactPageResponse;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IStockLocationRepository {
    StockLocation save(StockLocation entity);

    List<StockLocation> saveAll(List<StockLocation> entities);

    StockLocation update(StockLocation entity);

    List<StockLocation> updateAll(List<StockLocation> entities);

    void delete(StockLocation entity);

    void deleteById(UUID id);

    void deleteAll(List<StockLocation> entities);

    void softDeleteById(UUID id);

    Optional<StockLocation> findById(UUID id);

    List<StockLocation> findAll();

    ExactPageResponse<StockLocation> getStockLocationExactPage(ExactPageRequest request);
}
