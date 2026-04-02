package io.github.nguyennhatquang.fashion.Inventory.usecase.AdapterUseCase;

import java.util.List;
import java.util.UUID;
import java.util.Set;

import org.springframework.stereotype.Service;

import io.github.nguyennhatquang.fashion.Inventory.delivery.Dto.OutboxEventInventory.OutboxEventInventoryRequest.OutboxEventInventoryCreateRequest;
import io.github.nguyennhatquang.fashion.Inventory.delivery.Dto.OutboxEventInventory.OutboxEventInventoryRequest.OutboxEventInventoryUpdateRequest;
import io.github.nguyennhatquang.fashion.Inventory.delivery.Mapper.OutboxEventInventoryMapper;
import io.github.nguyennhatquang.fashion.Inventory.domain.IRepository.postgres.IOutboxEventInventoryRepository;
import io.github.nguyennhatquang.fashion.Inventory.domain.entity.InventorySummary;
import io.github.nguyennhatquang.fashion.Inventory.domain.entity.OutboxEventInventory;
import io.github.nguyennhatquang.fashion.Inventory.usecase.IUseCase.IAdminOutboxEventInventoryUseCase;
import io.github.nguyennhatquang.fashion.common.infrastructure.Function.JpaExactPagePaginationService;
import io.github.nguyennhatquang.fashion.common.request.ExactPageRequestv2;
import io.github.nguyennhatquang.fashion.common.response.ExactPageResponse;
import io.github.nguyennhatquang.fashion.common.response.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminOutboxEventInventoryUseCase implements IAdminOutboxEventInventoryUseCase {
    private final IOutboxEventInventoryRepository outboxRepository;
    private final OutboxEventInventoryMapper outboxMapper;

        private final JpaExactPagePaginationService paginationService;

    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
            "createdAt", "updatedAt");

    private static final Set<String> ALLOWED_FILTER_FIELDS = Set.of(
            "aggregateId", "aggregateType", "type");

    @Override
    public Result<OutboxEventInventory, Exception> createOutboxEvent(OutboxEventInventoryCreateRequest request) {
        try {
            OutboxEventInventory event = outboxMapper.toEntity(request);
            outboxRepository.save(event);
            return Result.success(event);
        } catch (Exception e) {
            return Result.error(e);
        }
    }

    @Override
    public Result<OutboxEventInventory, Exception> updateOutboxEventStatus(OutboxEventInventoryUpdateRequest request,
            UUID id) {
        try {
            OutboxEventInventory event = outboxRepository.findById(id).orElse(null);
            if (event == null) {
                return Result.error(new Exception("OutboxEventInventory not found"));
            }
            outboxMapper.updateEntityFromRequest(request, event);
            outboxRepository.save(event);
            return Result.success(event);
        } catch (Exception e) {
            return Result.error(e);
        }
    }

    @Override
    public Result<OutboxEventInventory, Exception> getOutboxEventById(UUID id) {
        try {
            OutboxEventInventory event = outboxRepository.findById(id).orElse(null);
            if (event == null) {
                return Result.error(new Exception("OutboxEventInventory not found"));
            }
            return Result.success(event);
        } catch (Exception e) {
            return Result.error(e);
        }
    }

    @Override
    public Result<ExactPageResponse<OutboxEventInventory>, Exception> getAllOutboxEvents(ExactPageRequestv2 request) {
        try {
            ExactPageResponse<OutboxEventInventory> response = paginationService.execute(
                    request,
                    OutboxEventInventory.class,
                    ALLOWED_SORT_FIELDS,
                    ALLOWED_FILTER_FIELDS
            );
            return Result.success(response);
        } catch (Exception e) {
            return Result.error(e);
        }
    }
}
