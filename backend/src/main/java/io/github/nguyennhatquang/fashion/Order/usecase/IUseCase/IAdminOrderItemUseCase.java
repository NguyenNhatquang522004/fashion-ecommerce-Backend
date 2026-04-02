package io.github.nguyennhatquang.fashion.Order.usecase.IUseCase;

import java.util.UUID;

import io.github.nguyennhatquang.fashion.Order.delivery.Dto.OrderItem.OrderItemRequest.OrderItemCreateRequest;
import io.github.nguyennhatquang.fashion.Order.delivery.Dto.OrderItem.OrderItemRequest.OrderItemUpdateRequest;
import io.github.nguyennhatquang.fashion.Order.domain.entity.OrderItem;
import io.github.nguyennhatquang.fashion.common.request.ExactPageRequestv2;
import io.github.nguyennhatquang.fashion.common.response.ExactPageResponse;
import io.github.nguyennhatquang.fashion.common.response.Result;

public interface IAdminOrderItemUseCase {
    Result<OrderItem, Exception> createOrderItem(OrderItemCreateRequest request);

    Result<OrderItem, Exception> updateOrderItem(OrderItemUpdateRequest request, UUID id);

    Result<Void, Exception> deleteOrderItem(UUID id);

    Result<OrderItem, Exception> getOrderItemById(UUID id);

    Result<ExactPageResponse<OrderItem>, Exception> getAllOrderItems(ExactPageRequestv2 request);
}
