package io.github.nguyennhatquang.fashion.Inventory.usecase.IStrategy;

import io.github.nguyennhatquang.fashion.Inventory.delivery.Dto.StockReservation.StockReservationRequest.StockReservationCreateRequestv2;
import io.github.nguyennhatquang.fashion.common.Enum.ReservationStatusEnum;
import io.github.nguyennhatquang.fashion.common.response.Result;

public interface IStrategyStockReservation {
    ReservationStatusEnum getType();

    Result<Void, Exception> execute(StockReservationCreateRequestv2 request);
}
