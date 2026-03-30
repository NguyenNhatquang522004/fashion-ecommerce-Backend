package io.github.nguyennhatquang.fashion.Inventory.usecase.AdapterUseCase;

import java.util.Optional;

import org.springframework.stereotype.Service;

import io.github.nguyennhatquang.fashion.Inventory.delivery.Dto.InventoryLedger.InventoryLedgerRequest.InventoryLedgerCreateRequest;
import io.github.nguyennhatquang.fashion.Inventory.delivery.Dto.StockReservation.StockReservationRequest.StockReservationCreateRequest;
import io.github.nguyennhatquang.fashion.Inventory.delivery.Dto.StockReservation.StockReservationRequest.StockReservationCreateRequestv2;
import io.github.nguyennhatquang.fashion.Inventory.delivery.Mapper.StockReservationMapper;
import io.github.nguyennhatquang.fashion.Inventory.domain.IRepository.postgres.IStockReservationRepository;
import io.github.nguyennhatquang.fashion.Inventory.domain.entity.StockReservation;
import io.github.nguyennhatquang.fashion.Inventory.usecase.IStrategy.IStrategyInventoryLedger;
import io.github.nguyennhatquang.fashion.Inventory.usecase.IStrategy.IStrategyStockReservation;
import io.github.nguyennhatquang.fashion.Inventory.usecase.IUseCase.IStockReservationUseCase;
import io.github.nguyennhatquang.fashion.Inventory.usecase.Strategy.InventoryLedger.StrategyInventoryLedger;
import io.github.nguyennhatquang.fashion.Inventory.usecase.Strategy.StockReservation.StrategyStockReservationFactory;
import io.github.nguyennhatquang.fashion.common.Enum.InventoryTransactionTypeEnum;
import io.github.nguyennhatquang.fashion.common.Enum.ReservationStatusEnum;
import io.github.nguyennhatquang.fashion.common.response.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class StockReservationUseCase implements IStockReservationUseCase {
    private final StrategyStockReservationFactory strategyStockReservationFactory;

    @Override
    public Result<StockReservation, Exception> createOrUpdateStockReservation(StockReservationCreateRequestv2 request) {
        try {

            IStrategyStockReservation strategy = strategyStockReservationFactory
                    .getStrategy(ReservationStatusEnum.RESERVED);
            Result<StockReservation, Exception> result = strategy.execute(request);
            if (result.hasError()) {
                return Result.error(result.error());
            }
            return Result.success(result.data());
        } catch (Exception e) {
            return Result.error(e);
        }
    }

}
