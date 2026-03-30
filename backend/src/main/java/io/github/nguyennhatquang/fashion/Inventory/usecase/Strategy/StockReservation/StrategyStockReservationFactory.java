package io.github.nguyennhatquang.fashion.Inventory.usecase.Strategy.StockReservation;

import java.util.EnumMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.github.nguyennhatquang.fashion.Inventory.usecase.IStrategy.IStrategyInventoryLedger;
import io.github.nguyennhatquang.fashion.Inventory.usecase.IStrategy.IStrategyStockReservation;
import io.github.nguyennhatquang.fashion.common.Enum.InventoryTransactionTypeEnum;
import io.github.nguyennhatquang.fashion.common.Enum.ReservationStatusEnum;
import jakarta.annotation.PostConstruct;

@Component
public class StrategyStockReservationFactory {


    private Map<ReservationStatusEnum, IStrategyStockReservation> strategyMap;

    public StrategyStockReservationFactory(Set<IStrategyStockReservation> strategies) {
        strategyMap = new EnumMap<>(ReservationStatusEnum.class);
        for (IStrategyStockReservation strategy : strategies) {
            strategyMap.put(strategy.getType(), strategy);
        }
    }

    public IStrategyStockReservation getStrategy(ReservationStatusEnum type) {
        IStrategyStockReservation strategy = strategyMap.get(type);
        if (strategy == null) {
            throw new IllegalArgumentException("No strategy found for type: " + type);
        }
        return strategy;
    }
}
