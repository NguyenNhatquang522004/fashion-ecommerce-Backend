package io.github.nguyennhatquang.fashion.Inventory.domain.IRepository.postgres;

import io.github.nguyennhatquang.fashion.Catalog.domain.entity.Product;
import io.github.nguyennhatquang.fashion.Inventory.domain.entity.StockReservation;
import io.github.nguyennhatquang.fashion.common.request.ExactPageRequest;
import io.github.nguyennhatquang.fashion.common.response.ExactPageResponse;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IStockReservationRepository {
    StockReservation save(StockReservation entity);

    List<StockReservation> saveAll(List<StockReservation> entities);

    StockReservation update(StockReservation entity);

    List<StockReservation> updateAll(List<StockReservation> entities);

    void delete(StockReservation entity);

    void deleteById(UUID id);

    void deleteAll(List<StockReservation> entities);

    void softDeleteById(UUID id);

    Optional<StockReservation> findById(UUID id);

    List<StockReservation> findAll();

    ExactPageResponse<StockReservation> getStockReservationExactPage(ExactPageRequest request);

    Optional<StockReservation> findByOrderIdAndSkuCodeAndWarehouseId(String orderId, String skuCode, UUID warehouseId);

    int expireExpiredReservations(OffsetDateTime now);

}
