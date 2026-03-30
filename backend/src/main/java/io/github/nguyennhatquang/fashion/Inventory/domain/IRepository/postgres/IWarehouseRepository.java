package io.github.nguyennhatquang.fashion.Inventory.domain.IRepository.postgres;

import io.github.nguyennhatquang.fashion.Catalog.domain.entity.Product;
import io.github.nguyennhatquang.fashion.Inventory.domain.entity.Warehouse;
import io.github.nguyennhatquang.fashion.common.request.ExactPageRequest;
import io.github.nguyennhatquang.fashion.common.response.ExactPageResponse;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IWarehouseRepository {
    Warehouse save(Warehouse entity);

    List<Warehouse> saveAll(List<Warehouse> entities);

    Warehouse update(Warehouse entity);

    List<Warehouse> updateAll(List<Warehouse> entities);

    void delete(Warehouse entity);

    void deleteById(UUID id);

    void deleteAll(List<Warehouse> entities);

    void softDeleteById(UUID id);

    Optional<Warehouse> findById(UUID id);

    List<Warehouse> findAll();

    ExactPageResponse<Warehouse> getWarehouseExactPage(ExactPageRequest request);
}
