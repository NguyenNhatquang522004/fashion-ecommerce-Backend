package io.github.nguyennhatquang.fashion.Order.usecase.AdapterUseCase;

import java.util.UUID;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import io.github.nguyennhatquang.fashion.Order.delivery.Dto.Order.OrderRequest.OrderCreateRequest;
import io.github.nguyennhatquang.fashion.Order.delivery.Dto.Order.OrderRequest.OrderUpdateRequest;
import io.github.nguyennhatquang.fashion.Order.delivery.Dto.OrderItem.OrderItemRequest.OrderItemCreateRequest;
import io.github.nguyennhatquang.fashion.Order.delivery.Mapper.OrderItemMapper;
import io.github.nguyennhatquang.fashion.Order.delivery.Mapper.OrderMapper;
import io.github.nguyennhatquang.fashion.Order.domain.IRepository.postgres.IOrderItemRepository;
import io.github.nguyennhatquang.fashion.Order.domain.IRepository.postgres.IOrderRepository;
import io.github.nguyennhatquang.fashion.Order.domain.entity.Order;
import io.github.nguyennhatquang.fashion.Order.domain.entity.OrderItem;
import io.github.nguyennhatquang.fashion.Order.usecase.IUseCase.IAdminOrderUseCase;
import io.github.nguyennhatquang.fashion.common.infrastructure.Function.JpaExactPagePaginationService;
import io.github.nguyennhatquang.fashion.common.request.ExactPageRequestv2;
import io.github.nguyennhatquang.fashion.common.response.ExactPageResponse;
import io.github.nguyennhatquang.fashion.common.response.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminOrderUseCase implements IAdminOrderUseCase {
    private final IOrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final JpaExactPagePaginationService paginationService;
    private final IOrderItemRepository orderItemRepository;
    private final OrderItemMapper orderItemMapper;
    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
            "createdAt", "updatedAt", "finalAmount");

    private static final Set<String> ALLOWED_FILTER_FIELDS = Set.of(
            "userId", "status", "orderCode");

    @Override
    public Result<Order, Exception> createOrder(OrderCreateRequest request,
            List<OrderItemCreateRequest> orderItemCreateRequests) {
        try {
            Order order = orderMapper.toEntity(request);
            for (OrderItemCreateRequest orderItemCreateRequest : orderItemCreateRequests) {
                OrderItem orderItem = orderItemMapper.toEntity(orderItemCreateRequest);
                orderItem.setOrder(order);
                order.getItems().add(orderItem);
            }
            orderRepository.save(order);
            return Result.success(order);
        } catch (Exception e) {
            log.error("Error creating Order", e);
            return Result.error(e);
        }
    }

    @Override
    public Result<Order, Exception> updateOrder(OrderUpdateRequest request, UUID id) {
        try {
            Order order = orderRepository.findById(id).orElse(null);
            if (order == null) {
                return Result.error(new Exception("Order not found"));
            }
            orderMapper.updateEntityFromRequest(request, order);
            orderRepository.save(order);
            return Result.success(order);
        } catch (Exception e) {
            log.error("Error updating Order", e);
            return Result.error(e);
        }
    }

    @Override
    public Result<Void, Exception> deleteOrder(UUID id) {
        try {
            orderRepository.deleteById(id);
            return Result.success(null);
        } catch (Exception e) {
            log.error("Error deleting Order", e);
            return Result.error(e);
        }
    }

    @Override
    public Result<Order, Exception> getOrderById(UUID id) {
        try {
            Order order = orderRepository.findById(id).orElse(null);
            if (order == null) {
                return Result.error(new Exception("Order not found"));
            }
            return Result.success(order);
        } catch (Exception e) {
            log.error("Error getting Order by id", e);
            return Result.error(e);
        }
    }

    @Override
    public Result<ExactPageResponse<Order>, Exception> getAllOrders(ExactPageRequestv2 request) {
        try {
            ExactPageResponse<Order> response = paginationService.execute(
                    request,
                    Order.class,
                    ALLOWED_SORT_FIELDS,
                    ALLOWED_FILTER_FIELDS);
            return Result.success(response);
        } catch (Exception e) {
            log.error("Error getting all Orders", e);
            return Result.error(e);
        }
    }
}
