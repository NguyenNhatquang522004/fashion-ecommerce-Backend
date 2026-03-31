package io.github.nguyennhatquang.fashion.Order.domain.IRepository.postgres;

import io.github.nguyennhatquang.fashion.Order.domain.entity.Shipment;
import io.github.nguyennhatquang.fashion.common.request.ExactPageRequest;
import io.github.nguyennhatquang.fashion.common.response.ExactPageResponse;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IShipmentRepository {
    Shipment save(Shipment entity);

    List<Shipment> saveAll(List<Shipment> entities);

    Shipment update(Shipment entity);

    List<Shipment> updateAll(List<Shipment> entities);

    void delete(Shipment entity);

    void deleteById(UUID id);

    void deleteAll(List<Shipment> entities);

    void softDeleteById(UUID id);

    Optional<Shipment> findById(UUID id);

    List<Shipment> findAll();

    ExactPageResponse<Shipment> getShipmentExactPage(ExactPageRequest request);
}
