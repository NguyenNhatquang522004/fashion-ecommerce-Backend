package io.github.nguyennhatquang.fashion.Order.domain.IRepository.postgres;

import io.github.nguyennhatquang.fashion.Order.domain.entity.Order;
import io.github.nguyennhatquang.fashion.common.request.ExactPageRequest;
import io.github.nguyennhatquang.fashion.common.response.ExactPageResponse;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IOrderRepository {
    Order save(Order entity);

    List<Order> saveAll(List<Order> entities);

    Order update(Order entity);

    List<Order> updateAll(List<Order> entities);

    void delete(Order entity);

    void deleteById(UUID id);

    void deleteAll(List<Order> entities);

    void softDeleteById(UUID id);

    Optional<Order> findById(UUID id);

    List<Order> findAll();

    ExactPageResponse<Order> getOrderExactPage(ExactPageRequest request);
}
