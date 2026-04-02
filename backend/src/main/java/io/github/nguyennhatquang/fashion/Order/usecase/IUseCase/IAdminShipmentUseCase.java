package io.github.nguyennhatquang.fashion.Order.usecase.IUseCase;

import java.util.UUID;

import io.github.nguyennhatquang.fashion.Order.delivery.Dto.Shipment.ShipmentRequest.ShipmentCreateRequest;
import io.github.nguyennhatquang.fashion.Order.delivery.Dto.Shipment.ShipmentRequest.ShipmentUpdateRequest;
import io.github.nguyennhatquang.fashion.Order.domain.entity.Shipment;
import io.github.nguyennhatquang.fashion.common.request.ExactPageRequestv2;
import io.github.nguyennhatquang.fashion.common.response.ExactPageResponse;
import io.github.nguyennhatquang.fashion.common.response.Result;

public interface IAdminShipmentUseCase {
    Result<Shipment, Exception> createShipment(ShipmentCreateRequest request);

    Result<Shipment, Exception> updateShipment(ShipmentUpdateRequest request, UUID id);

    Result<Void, Exception> deleteShipment(UUID id);

    Result<Shipment, Exception> getShipmentById(UUID id);

    Result<ExactPageResponse<Shipment>, Exception> getAllShipments(ExactPageRequestv2 request);
}
