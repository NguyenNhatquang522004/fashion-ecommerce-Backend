package io.github.nguyennhatquang.fashion.Order.usecase.AdapterUseCase;

import java.util.UUID;
import java.util.Set;

import org.springframework.stereotype.Service;

import io.github.nguyennhatquang.fashion.Order.delivery.Dto.Shipment.ShipmentRequest.ShipmentCreateRequest;
import io.github.nguyennhatquang.fashion.Order.delivery.Dto.Shipment.ShipmentRequest.ShipmentUpdateRequest;
import io.github.nguyennhatquang.fashion.Order.delivery.Mapper.ShipmentMapper;
import io.github.nguyennhatquang.fashion.Order.domain.IRepository.postgres.IShipmentRepository;
import io.github.nguyennhatquang.fashion.Order.domain.entity.Shipment;
import io.github.nguyennhatquang.fashion.Order.usecase.IUseCase.IAdminShipmentUseCase;
import io.github.nguyennhatquang.fashion.common.infrastructure.Function.JpaExactPagePaginationService;
import io.github.nguyennhatquang.fashion.common.request.ExactPageRequestv2;
import io.github.nguyennhatquang.fashion.common.response.ExactPageResponse;
import io.github.nguyennhatquang.fashion.common.response.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminShipmentUseCase implements IAdminShipmentUseCase {
    private final IShipmentRepository shipmentRepository;
    private final ShipmentMapper shipmentMapper;
    private final JpaExactPagePaginationService paginationService;

    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
            "createdAt", "updatedAt");

    private static final Set<String> ALLOWED_FILTER_FIELDS = Set.of(
            "orderId", "carrier", "status");

    @Override
    public Result<Shipment, Exception> createShipment(ShipmentCreateRequest request) {
        try {
            Shipment shipment = shipmentMapper.toEntity(request);
            shipmentRepository.save(shipment);
            return Result.success(shipment);
        } catch (Exception e) {
            log.error("Error creating Shipment", e);
            return Result.error(e);
        }
    }

    @Override
    public Result<Shipment, Exception> updateShipment(ShipmentUpdateRequest request, UUID id) {
        try {
            Shipment shipment = shipmentRepository.findById(id).orElse(null);
            if (shipment == null) {
                return Result.error(new Exception("Shipment not found"));
            }
            shipmentMapper.updateEntityFromRequest(request, shipment);
            shipmentRepository.save(shipment);
            return Result.success(shipment);
        } catch (Exception e) {
            log.error("Error updating Shipment", e);
            return Result.error(e);
        }
    }

    @Override
    public Result<Void, Exception> deleteShipment(UUID id) {
        try {
            shipmentRepository.deleteById(id);
            return Result.success(null);
        } catch (Exception e) {
            log.error("Error deleting Shipment", e);
            return Result.error(e);
        }
    }

    @Override
    public Result<Shipment, Exception> getShipmentById(UUID id) {
        try {
            Shipment shipment = shipmentRepository.findById(id).orElse(null);
            if (shipment == null) {
                return Result.error(new Exception("Shipment not found"));
            }
            return Result.success(shipment);
        } catch (Exception e) {
            log.error("Error getting Shipment by id", e);
            return Result.error(e);
        }
    }

    @Override
    public Result<ExactPageResponse<Shipment>, Exception> getAllShipments(ExactPageRequestv2 request) {
        try {
            ExactPageResponse<Shipment> response = paginationService.execute(
                    request,
                    Shipment.class,
                    ALLOWED_SORT_FIELDS,
                    ALLOWED_FILTER_FIELDS
            );
            return Result.success(response);
        } catch (Exception e) {
            log.error("Error getting all Shipments", e);
            return Result.error(e);
        }
    }
}
