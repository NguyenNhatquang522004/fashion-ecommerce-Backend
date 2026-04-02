package io.github.nguyennhatquang.fashion.Order.usecase.AdapterUseCase;

import java.util.UUID;
import java.util.Set;

import org.springframework.stereotype.Service;

import io.github.nguyennhatquang.fashion.Order.delivery.Dto.SagaState.SagaStateRequest.SagaStateCreateRequest;
import io.github.nguyennhatquang.fashion.Order.delivery.Dto.SagaState.SagaStateRequest.SagaStateUpdateRequest;
import io.github.nguyennhatquang.fashion.Order.delivery.Mapper.SagaStateMapper;
import io.github.nguyennhatquang.fashion.Order.domain.IRepository.postgres.ISagaStateRepository;
import io.github.nguyennhatquang.fashion.Order.domain.entity.SagaState;
import io.github.nguyennhatquang.fashion.Order.usecase.IUseCase.IAdminSagaStateUseCase;
import io.github.nguyennhatquang.fashion.common.infrastructure.Function.JpaExactPagePaginationService;
import io.github.nguyennhatquang.fashion.common.request.ExactPageRequestv2;
import io.github.nguyennhatquang.fashion.common.response.ExactPageResponse;
import io.github.nguyennhatquang.fashion.common.response.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminSagaStateUseCase implements IAdminSagaStateUseCase {
    private final ISagaStateRepository sagaStateRepository;
    private final SagaStateMapper sagaStateMapper;
    private final JpaExactPagePaginationService paginationService;

    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
            "createdAt", "updatedAt");

    private static final Set<String> ALLOWED_FILTER_FIELDS = Set.of(
            "orderId", "currentStep", "sagaStatus");

    @Override
    public Result<SagaState, Exception> createSagaState(SagaStateCreateRequest request) {
        try {
            SagaState sagaState = sagaStateMapper.toEntity(request);
            sagaStateRepository.save(sagaState);
            return Result.success(sagaState);
        } catch (Exception e) {
            log.error("Error creating SagaState", e);
            return Result.error(e);
        }
    }

    @Override
    public Result<SagaState, Exception> updateSagaState(SagaStateUpdateRequest request, UUID id) {
        try {
            SagaState sagaState = sagaStateRepository.findById(id).orElse(null);
            if (sagaState == null) {
                return Result.error(new Exception("SagaState not found"));
            }
            sagaStateMapper.updateEntityFromRequest(request, sagaState);
            sagaStateRepository.save(sagaState);
            return Result.success(sagaState);
        } catch (Exception e) {
            log.error("Error updating SagaState", e);
            return Result.error(e);
        }
    }

    @Override
    public Result<Void, Exception> deleteSagaState(UUID id) {
        try {
            sagaStateRepository.deleteById(id);
            return Result.success(null);
        } catch (Exception e) {
            log.error("Error deleting SagaState", e);
            return Result.error(e);
        }
    }

    @Override
    public Result<SagaState, Exception> getSagaStateById(UUID id) {
        try {
            SagaState sagaState = sagaStateRepository.findById(id).orElse(null);
            if (sagaState == null) {
                return Result.error(new Exception("SagaState not found"));
            }
            return Result.success(sagaState);
        } catch (Exception e) {
            log.error("Error getting SagaState by id", e);
            return Result.error(e);
        }
    }

    @Override
    public Result<ExactPageResponse<SagaState>, Exception> getAllSagaStates(ExactPageRequestv2 request) {
        try {
            ExactPageResponse<SagaState> response = paginationService.execute(
                    request,
                    SagaState.class,
                    ALLOWED_SORT_FIELDS,
                    ALLOWED_FILTER_FIELDS
            );
            return Result.success(response);
        } catch (Exception e) {
            log.error("Error getting all SagaStates", e);
            return Result.error(e);
        }
    }
}
