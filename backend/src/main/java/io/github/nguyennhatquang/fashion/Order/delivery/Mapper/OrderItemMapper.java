package io.github.nguyennhatquang.fashion.Order.delivery.Mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import io.github.nguyennhatquang.fashion.Order.delivery.Dto.OrderItem.OrderItemRequest.OrderItemCreateRequest;
import io.github.nguyennhatquang.fashion.Order.delivery.Dto.OrderItem.OrderItemRequest.OrderItemUpdateRequest;
import io.github.nguyennhatquang.fashion.Order.delivery.Dto.OrderItem.OrderItemResponse;
import io.github.nguyennhatquang.fashion.Order.domain.entity.OrderItem;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface OrderItemMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isDeleted", constant = "false")
    @Mapping(target = "subtotal", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    OrderItem toEntity(OrderItemCreateRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "subtotal", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    void updateEntityFromRequest(OrderItemUpdateRequest request, @MappingTarget OrderItem entity);

    OrderItemResponse toResponse(OrderItem entity);

    List<OrderItemResponse> toResponseList(List<OrderItem> entities);
}
