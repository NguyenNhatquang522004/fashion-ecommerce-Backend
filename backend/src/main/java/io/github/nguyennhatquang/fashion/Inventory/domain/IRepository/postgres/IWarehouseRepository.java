package io.github.nguyennhatquang.fashion.Inventory.domain.IRepository.postgres;

import io.github.nguyennhatquang.fashion.Inventory.domain.entity.Warehouse;

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
}
