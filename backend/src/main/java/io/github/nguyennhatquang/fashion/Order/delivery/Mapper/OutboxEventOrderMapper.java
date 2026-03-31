package io.github.nguyennhatquang.fashion.Order.delivery.Mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import io.github.nguyennhatquang.fashion.Order.delivery.Dto.OutboxEventOrder.OutboxEventOrderRequest.OutboxEventOrderCreateRequest;
import io.github.nguyennhatquang.fashion.Order.delivery.Dto.OutboxEventOrder.OutboxEventOrderRequest.OutboxEventOrderUpdateRequest;
import io.github.nguyennhatquang.fashion.Order.delivery.Dto.OutboxEventOrder.OutboxEventOrderResponse;
import io.github.nguyennhatquang.fashion.Order.domain.entity.OutboxEventOrder;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface OutboxEventOrderMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isDeleted", constant = "false")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    OutboxEventOrder toEntity(OutboxEventOrderCreateRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    void updateEntityFromRequest(OutboxEventOrderUpdateRequest request, @MappingTarget OutboxEventOrder entity);

    OutboxEventOrderResponse toResponse(OutboxEventOrder entity);

    List<OutboxEventOrderResponse> toResponseList(List<OutboxEventOrder> entities);
}
