package io.github.nguyennhatquang.fashion.Inventory.usecase.Strategy.StockReservation;

import org.springframework.stereotype.Service;

import io.github.nguyennhatquang.fashion.Inventory.delivery.Dto.StockReservation.StockReservationRequest.StockReservationCreateRequestv2;
import io.github.nguyennhatquang.fashion.Inventory.domain.entity.StockReservation;
import io.github.nguyennhatquang.fashion.Inventory.usecase.IStrategy.IStrategyStockReservation;
import io.github.nguyennhatquang.fashion.common.Enum.ReservationStatusEnum;
import io.github.nguyennhatquang.fashion.common.response.Result;
import lombok.RequiredArgsConstructor;
@Service
@RequiredArgsConstructor
public class EXPIRED implements IStrategyStockReservation {

    @Override
    public ReservationStatusEnum getType() {
        return ReservationStatusEnum.EXPIRED;
    }

    @Override
    public Result<StockReservation, Exception> execute(StockReservationCreateRequestv2 request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'execute'");
    }

}
