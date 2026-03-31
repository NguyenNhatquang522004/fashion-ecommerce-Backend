package io.github.nguyennhatquang.fashion.Order.domain.IRepository.postgres;

import io.github.nguyennhatquang.fashion.Order.domain.entity.OrderItem;
import io.github.nguyennhatquang.fashion.common.request.ExactPageRequest;
import io.github.nguyennhatquang.fashion.common.response.ExactPageResponse;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IOrderItemRepository {
    OrderItem save(OrderItem entity);

    List<OrderItem> saveAll(List<OrderItem> entities);

    OrderItem update(OrderItem entity);

    List<OrderItem> updateAll(List<OrderItem> entities);

    void delete(OrderItem entity);

    void deleteById(UUID id);

    void deleteAll(List<OrderItem> entities);

    void softDeleteById(UUID id);

    Optional<OrderItem> findById(UUID id);

    List<OrderItem> findAll();

    ExactPageResponse<OrderItem> getOrderItemExactPage(ExactPageRequest request);
}
