package io.github.nguyennhatquang.fashion.Inventory.usecase.IStrategy;

import io.github.nguyennhatquang.fashion.Inventory.domain.entity.StockReservation;
import io.github.nguyennhatquang.fashion.common.Enum.ReservationStatusEnum;
import io.github.nguyennhatquang.fashion.common.Payload.inventory.StockReservationCreatePayload;
import io.github.nguyennhatquang.fashion.common.response.Result;

public interface IStrategyStockReservation {
    ReservationStatusEnum getType();

    Result<StockReservation, Exception> execute(StockReservationCreatePayload request);
}
