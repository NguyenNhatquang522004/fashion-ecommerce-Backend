package io.github.nguyennhatquang.fashion.Inventory.domain.IRepository.postgres;

import io.github.nguyennhatquang.fashion.Catalog.domain.entity.Product;
import io.github.nguyennhatquang.fashion.Inventory.domain.entity.InventoryLedger;
import io.github.nguyennhatquang.fashion.common.request.ExactPageRequest;
import io.github.nguyennhatquang.fashion.common.response.ExactPageResponse;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IInventoryLedgerRepository {
    InventoryLedger save(InventoryLedger entity);

    List<InventoryLedger> saveAll(List<InventoryLedger> entities);

    InventoryLedger update(InventoryLedger entity);

    List<InventoryLedger> updateAll(List<InventoryLedger> entities);

    void delete(InventoryLedger entity);

    void deleteById(UUID id);

    void deleteAll(List<InventoryLedger> entities);

    void softDeleteById(UUID id);

    Optional<InventoryLedger> findById(UUID id);

    List<InventoryLedger> findAll();

    ExactPageResponse<InventoryLedger> getInventoryLedgerExactPage(ExactPageRequest request);
}
