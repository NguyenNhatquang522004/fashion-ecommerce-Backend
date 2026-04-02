package io.github.nguyennhatquang.fashion.Order.usecase.IUseCase;

import java.util.UUID;

import io.github.nguyennhatquang.fashion.Order.delivery.Dto.OutboxEventOrder.OutboxEventOrderRequest.OutboxEventOrderCreateRequest;
import io.github.nguyennhatquang.fashion.Order.delivery.Dto.OutboxEventOrder.OutboxEventOrderRequest.OutboxEventOrderUpdateRequest;
import io.github.nguyennhatquang.fashion.Order.domain.entity.OutboxEventOrder;
import io.github.nguyennhatquang.fashion.common.request.ExactPageRequestv2;
import io.github.nguyennhatquang.fashion.common.response.ExactPageResponse;
import io.github.nguyennhatquang.fashion.common.response.Result;

public interface IAdminOutboxEventOrderUseCase {
    Result<OutboxEventOrder, Exception> createOutboxEventOrder(OutboxEventOrderCreateRequest request);

    Result<OutboxEventOrder, Exception> updateOutboxEventOrder(OutboxEventOrderUpdateRequest request, UUID id);

    Result<Void, Exception> deleteOutboxEventOrder(UUID id);

    Result<OutboxEventOrder, Exception> getOutboxEventOrderById(UUID id);

    Result<ExactPageResponse<OutboxEventOrder>, Exception> getAllOutboxEventOrders(ExactPageRequestv2 request);
}
