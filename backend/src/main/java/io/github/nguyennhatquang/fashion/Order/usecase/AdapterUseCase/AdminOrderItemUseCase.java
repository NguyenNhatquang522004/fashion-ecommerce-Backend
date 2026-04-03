package io.github.nguyennhatquang.fashion.Order.usecase.AdapterUseCase;

import java.util.UUID;
import java.util.Set;

import org.springframework.stereotype.Service;

import io.github.nguyennhatquang.fashion.Order.delivery.Dto.OrderItem.OrderItemRequest.OrderItemCreateRequest;
import io.github.nguyennhatquang.fashion.Order.delivery.Dto.OrderItem.OrderItemRequest.OrderItemUpdateRequest;
import io.github.nguyennhatquang.fashion.Order.delivery.Mapper.OrderItemMapper;
import io.github.nguyennhatquang.fashion.Order.delivery.Mapper.OrderMapper;
import io.github.nguyennhatquang.fashion.Order.domain.IRepository.postgres.IOrderItemRepository;
import io.github.nguyennhatquang.fashion.Order.domain.IRepository.postgres.IOrderRepository;
import io.github.nguyennhatquang.fashion.Order.domain.entity.Order;
import io.github.nguyennhatquang.fashion.Order.domain.entity.OrderItem;
import io.github.nguyennhatquang.fashion.Order.usecase.IUseCase.IAdminOrderItemUseCase;
import io.github.nguyennhatquang.fashion.common.infrastructure.Function.JpaExactPagePaginationService;
import io.github.nguyennhatquang.fashion.common.request.ExactPageRequestv2;
import io.github.nguyennhatquang.fashion.common.response.ExactPageResponse;
import io.github.nguyennhatquang.fashion.common.response.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminOrderItemUseCase implements IAdminOrderItemUseCase {
    private final IOrderItemRepository orderItemRepository;
    private final OrderItemMapper orderItemMapper;
    private final JpaExactPagePaginationService paginationService;
    private final IOrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
            "createdAt", "updatedAt", "unitPrice");

    private static final Set<String> ALLOWED_FILTER_FIELDS = Set.of(
            "orderId", "productId", "skuCode");

    @Override
    public Result<OrderItem, Exception> createOrderItem(OrderItemCreateRequest request) {
        try {
            Order order = orderRepository.findById(request.orderId()).orElse(null);
            if (order == null) {
                return Result.error(new Exception("Order not found"));
            }
            OrderItem orderItem = orderItemMapper.toEntity(request);
            order.addOrderItem(orderItem);
            orderRepository.save(order);
            return Result.success(orderItem);
        } catch (Exception e) {
            log.error("Error creating OrderItem", e);
            return Result.error(e);
        }
    }

    @Override
    public Result<OrderItem, Exception> updateOrderItem(OrderItemUpdateRequest request, UUID id) {
        try {
            OrderItem orderItem = orderItemRepository.findById(id).orElse(null);
            if (orderItem == null) {
                return Result.error(new Exception("OrderItem not found"));
            }
            orderItemMapper.updateEntityFromRequest(request, orderItem);
            orderItemRepository.save(orderItem);
            return Result.success(orderItem);
        } catch (Exception e) {
            log.error("Error updating OrderItem", e);
            return Result.error(e);
        }
    }

    @Override
    public Result<Void, Exception> deleteOrderItem(UUID id) {
        try {
            orderItemRepository.deleteById(id);
            return Result.success(null);
        } catch (Exception e) {
            log.error("Error deleting OrderItem", e);
            return Result.error(e);
        }
    }

    @Override
    public Result<OrderItem, Exception> getOrderItemById(UUID id) {
        try {
            OrderItem orderItem = orderItemRepository.findById(id).orElse(null);
            if (orderItem == null) {
                return Result.error(new Exception("OrderItem not found"));
            }
            return Result.success(orderItem);
        } catch (Exception e) {
            log.error("Error getting OrderItem by id", e);
            return Result.error(e);
        }
    }

    @Override
    public Result<ExactPageResponse<OrderItem>, Exception> getAllOrderItems(ExactPageRequestv2 request) {
        try {
            ExactPageResponse<OrderItem> response = paginationService.execute(
                    request,
                    OrderItem.class,
                    ALLOWED_SORT_FIELDS,
                    ALLOWED_FILTER_FIELDS);
            return Result.success(response);
        } catch (Exception e) {
            log.error("Error getting all OrderItems", e);
            return Result.error(e);
        }
    }
}
