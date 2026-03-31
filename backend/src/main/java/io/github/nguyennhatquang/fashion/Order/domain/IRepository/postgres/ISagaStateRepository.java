package io.github.nguyennhatquang.fashion.Order.domain.IRepository.postgres;

import io.github.nguyennhatquang.fashion.Order.domain.entity.SagaState;
import io.github.nguyennhatquang.fashion.common.request.ExactPageRequest;
import io.github.nguyennhatquang.fashion.common.response.ExactPageResponse;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ISagaStateRepository {
    SagaState save(SagaState entity);

    List<SagaState> saveAll(List<SagaState> entities);

    SagaState update(SagaState entity);

    List<SagaState> updateAll(List<SagaState> entities);

    void delete(SagaState entity);

    void deleteById(UUID id);

    void deleteAll(List<SagaState> entities);

    void softDeleteById(UUID id);

    Optional<SagaState> findById(UUID id);

    List<SagaState> findAll();

    ExactPageResponse<SagaState> getSagaStateExactPage(ExactPageRequest request);
}
