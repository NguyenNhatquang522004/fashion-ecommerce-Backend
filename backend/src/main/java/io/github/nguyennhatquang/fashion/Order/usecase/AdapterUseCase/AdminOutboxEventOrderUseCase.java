package io.github.nguyennhatquang.fashion.Order.usecase.AdapterUseCase;

import java.util.UUID;
import java.util.Set;

import org.springframework.stereotype.Service;

import io.github.nguyennhatquang.fashion.Order.delivery.Dto.OutboxEventOrder.OutboxEventOrderRequest.OutboxEventOrderCreateRequest;
import io.github.nguyennhatquang.fashion.Order.delivery.Dto.OutboxEventOrder.OutboxEventOrderRequest.OutboxEventOrderUpdateRequest;
import io.github.nguyennhatquang.fashion.Order.delivery.Mapper.OutboxEventOrderMapper;
import io.github.nguyennhatquang.fashion.Order.domain.IRepository.postgres.IOutboxEventOrderRepository;
import io.github.nguyennhatquang.fashion.Order.domain.entity.OutboxEventOrder;
import io.github.nguyennhatquang.fashion.Order.usecase.IUseCase.IAdminOutboxEventOrderUseCase;
import io.github.nguyennhatquang.fashion.common.infrastructure.Function.JpaExactPagePaginationService;
import io.github.nguyennhatquang.fashion.common.request.ExactPageRequestv2;
import io.github.nguyennhatquang.fashion.common.response.ExactPageResponse;
import io.github.nguyennhatquang.fashion.common.response.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminOutboxEventOrderUseCase implements IAdminOutboxEventOrderUseCase {
    private final IOutboxEventOrderRepository outboxEventOrderRepository;
    private final OutboxEventOrderMapper outboxEventOrderMapper;
    private final JpaExactPagePaginationService paginationService;

    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
            "createdAt", "updatedAt");

    private static final Set<String> ALLOWED_FILTER_FIELDS = Set.of(
            "aggregateId", "type", "status");

    @Override
    public Result<OutboxEventOrder, Exception> createOutboxEventOrder(OutboxEventOrderCreateRequest request) {
        try {
            OutboxEventOrder outboxEventOrder = outboxEventOrderMapper.toEntity(request);
            outboxEventOrderRepository.save(outboxEventOrder);
            return Result.success(outboxEventOrder);
        } catch (Exception e) {
            log.error("Error creating OutboxEventOrder", e);
            return Result.error(e);
        }
    }

    @Override
    public Result<OutboxEventOrder, Exception> updateOutboxEventOrder(OutboxEventOrderUpdateRequest request, UUID id) {
        try {
            OutboxEventOrder outboxEventOrder = outboxEventOrderRepository.findById(id).orElse(null);
            if (outboxEventOrder == null) {
                return Result.error(new Exception("OutboxEventOrder not found"));
            }
            outboxEventOrderMapper.updateEntityFromRequest(request, outboxEventOrder);
            outboxEventOrderRepository.save(outboxEventOrder);
            return Result.success(outboxEventOrder);
        } catch (Exception e) {
            log.error("Error updating OutboxEventOrder", e);
            return Result.error(e);
        }
    }

    @Override
    public Result<Void, Exception> deleteOutboxEventOrder(UUID id) {
        try {
            outboxEventOrderRepository.deleteById(id);
            return Result.success(null);
        } catch (Exception e) {
            log.error("Error deleting OutboxEventOrder", e);
            return Result.error(e);
        }
    }

    @Override
    public Result<OutboxEventOrder, Exception> getOutboxEventOrderById(UUID id) {
        try {
            OutboxEventOrder outboxEventOrder = outboxEventOrderRepository.findById(id).orElse(null);
            if (outboxEventOrder == null) {
                return Result.error(new Exception("OutboxEventOrder not found"));
            }
            return Result.success(outboxEventOrder);
        } catch (Exception e) {
            log.error("Error getting OutboxEventOrder by id", e);
            return Result.error(e);
        }
    }

    @Override
    public Result<ExactPageResponse<OutboxEventOrder>, Exception> getAllOutboxEventOrders(ExactPageRequestv2 request) {
        try {
            ExactPageResponse<OutboxEventOrder> response = paginationService.execute(
                    request,
                    OutboxEventOrder.class,
                    ALLOWED_SORT_FIELDS,
                    ALLOWED_FILTER_FIELDS
            );
            return Result.success(response);
        } catch (Exception e) {
            log.error("Error getting all OutboxEventOrders", e);
            return Result.error(e);
        }
    }
}
