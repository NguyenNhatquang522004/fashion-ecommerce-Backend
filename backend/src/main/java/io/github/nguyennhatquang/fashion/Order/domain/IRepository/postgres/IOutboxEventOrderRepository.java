package io.github.nguyennhatquang.fashion.Order.domain.IRepository.postgres;

import io.github.nguyennhatquang.fashion.Order.domain.entity.OutboxEventOrder;
import io.github.nguyennhatquang.fashion.common.request.ExactPageRequest;
import io.github.nguyennhatquang.fashion.common.response.ExactPageResponse;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IOutboxEventOrderRepository {
    OutboxEventOrder save(OutboxEventOrder entity);

    List<OutboxEventOrder> saveAll(List<OutboxEventOrder> entities);

    OutboxEventOrder update(OutboxEventOrder entity);

    List<OutboxEventOrder> updateAll(List<OutboxEventOrder> entities);

    void delete(OutboxEventOrder entity);

    void deleteById(UUID id);

    void deleteAll(List<OutboxEventOrder> entities);

    void softDeleteById(UUID id);

    Optional<OutboxEventOrder> findById(UUID id);

    List<OutboxEventOrder> findAll();

    ExactPageResponse<OutboxEventOrder> getOutboxEventOrderExactPage(ExactPageRequest request);
}
