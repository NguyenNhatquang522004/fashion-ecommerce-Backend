package io.github.nguyennhatquang.fashion.Order.delivery.Mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import io.github.nguyennhatquang.fashion.Order.delivery.Dto.Shipment.ShipmentRequest.ShipmentCreateRequest;
import io.github.nguyennhatquang.fashion.Order.delivery.Dto.Shipment.ShipmentRequest.ShipmentUpdateRequest;
import io.github.nguyennhatquang.fashion.Order.delivery.Dto.Shipment.ShipmentResponse;
import io.github.nguyennhatquang.fashion.Order.domain.entity.Shipment;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ShipmentMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isDeleted", constant = "false")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    Shipment toEntity(ShipmentCreateRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    void updateEntityFromRequest(ShipmentUpdateRequest request, @MappingTarget Shipment entity);

    ShipmentResponse toResponse(Shipment entity);

    List<ShipmentResponse> toResponseList(List<Shipment> entities);
}
