package io.github.nguyennhatquang.fashion.Inventory.usecase.IUseCase;

import io.github.nguyennhatquang.fashion.Inventory.domain.entity.StockReservation;
import io.github.nguyennhatquang.fashion.common.Payload.inventory.StockReservationCreatePayload;
import io.github.nguyennhatquang.fashion.common.response.Result;

public interface IStockReservationUseCase {
    Result<StockReservation, Exception> createOrUpdateStockReservation(StockReservationCreatePayload request);
}
