package io.github.nguyennhatquang.fashion.Inventory.usecase.IUseCase;

import io.github.nguyennhatquang.fashion.Inventory.delivery.Dto.StockReservation.StockReservationRequest.StockReservationCreateRequest;
import io.github.nguyennhatquang.fashion.Inventory.domain.entity.StockReservation;
import io.github.nguyennhatquang.fashion.common.response.Result;

public interface IStockReservationUseCase {
    Result<StockReservation, Exception> createStockReservation(StockReservationCreateRequest request);
}
