package io.github.nguyennhatquang.fashion.Order.usecase.IUseCase;

import java.util.List;
import java.util.UUID;

import io.github.nguyennhatquang.fashion.Order.delivery.Dto.Order.OrderRequest.OrderCreateRequest;
import io.github.nguyennhatquang.fashion.Order.delivery.Dto.Order.OrderRequest.OrderUpdateRequest;
import io.github.nguyennhatquang.fashion.Order.delivery.Dto.OrderItem.OrderItemRequest.OrderItemCreateRequest;
import io.github.nguyennhatquang.fashion.Order.domain.entity.Order;
import io.github.nguyennhatquang.fashion.common.request.ExactPageRequestv2;
import io.github.nguyennhatquang.fashion.common.response.ExactPageResponse;
import io.github.nguyennhatquang.fashion.common.response.Result;

public interface IAdminOrderUseCase {
    Result<Order, Exception> createOrder(OrderCreateRequest request,
            List<OrderItemCreateRequest> orderItemCreateRequests);

    Result<Order, Exception> updateOrder(OrderUpdateRequest request, UUID id);

    Result<Void, Exception> deleteOrder(UUID id);

    Result<Order, Exception> getOrderById(UUID id);

    Result<ExactPageResponse<Order>, Exception> getAllOrders(ExactPageRequestv2 request);
}
