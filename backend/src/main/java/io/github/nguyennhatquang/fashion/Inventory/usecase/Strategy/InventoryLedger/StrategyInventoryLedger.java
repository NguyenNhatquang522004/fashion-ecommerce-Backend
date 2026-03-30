package io.github.nguyennhatquang.fashion.Inventory.usecase.Strategy.InventoryLedger;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import io.github.nguyennhatquang.fashion.Inventory.usecase.IStrategy.IStrategyInventoryLedger;
import io.github.nguyennhatquang.fashion.common.Enum.InventoryTransactionTypeEnum;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class StrategyInventoryLedger {
    private final Map<InventoryTransactionTypeEnum, IStrategyInventoryLedger> strategyMap;

    public StrategyInventoryLedger(List<IStrategyInventoryLedger> strategies) {
        strategyMap = new EnumMap<>(InventoryTransactionTypeEnum.class);
        for (IStrategyInventoryLedger strategy : strategies) {
            strategyMap.put(strategy.getType(), strategy);
        }
    }

    public IStrategyInventoryLedger getStrategy(InventoryTransactionTypeEnum type) {
        IStrategyInventoryLedger strategy = strategyMap.get(type);
        if (strategy == null) {
            throw new IllegalArgumentException("Không hỗ trợ loại giao dịch: " + type);
        }
        return strategy;
    }
}
