package io.github.nguyennhatquang.fashion.Order.usecase.IUseCase;

import java.util.UUID;

import io.github.nguyennhatquang.fashion.Order.delivery.Dto.SagaState.SagaStateRequest.SagaStateCreateRequest;
import io.github.nguyennhatquang.fashion.Order.delivery.Dto.SagaState.SagaStateRequest.SagaStateUpdateRequest;
import io.github.nguyennhatquang.fashion.Order.domain.entity.SagaState;
import io.github.nguyennhatquang.fashion.common.request.ExactPageRequestv2;
import io.github.nguyennhatquang.fashion.common.response.ExactPageResponse;
import io.github.nguyennhatquang.fashion.common.response.Result;

public interface IAdminSagaStateUseCase {
    Result<SagaState, Exception> createSagaState(SagaStateCreateRequest request);

    Result<SagaState, Exception> updateSagaState(SagaStateUpdateRequest request, UUID id);

    Result<Void, Exception> deleteSagaState(UUID id);

    Result<SagaState, Exception> getSagaStateById(UUID id);

    Result<ExactPageResponse<SagaState>, Exception> getAllSagaStates(ExactPageRequestv2 request);
}
