package io.github.nguyennhatquang.fashion.Order.delivery.Mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import io.github.nguyennhatquang.fashion.Order.delivery.Dto.Order.OrderRequest.OrderCreateRequest;
import io.github.nguyennhatquang.fashion.Order.delivery.Dto.Order.OrderRequest.OrderUpdateRequest;
import io.github.nguyennhatquang.fashion.Order.delivery.Dto.Order.OrderResponse;
import io.github.nguyennhatquang.fashion.Order.domain.entity.Order;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface OrderMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isDeleted", constant = "false")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    Order toEntity(OrderCreateRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    void updateEntityFromRequest(OrderUpdateRequest request, @MappingTarget Order entity);

    OrderResponse toResponse(Order entity);

    List<OrderResponse> toResponseList(List<Order> entities);
}
