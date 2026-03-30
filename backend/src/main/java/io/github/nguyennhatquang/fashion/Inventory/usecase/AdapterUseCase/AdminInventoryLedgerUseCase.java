package io.github.nguyennhatquang.fashion.Inventory.usecase.AdapterUseCase;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import io.github.nguyennhatquang.fashion.Inventory.delivery.Dto.InventoryLedger.InventoryLedgerRequest.InventoryLedgerCreateRequest;
import io.github.nguyennhatquang.fashion.Inventory.delivery.Dto.InventorySummary.InventorySummaryRequest.InventorySummaryCreateRequest;
import io.github.nguyennhatquang.fashion.Inventory.delivery.Dto.InventorySummary.InventorySummaryRequest.InventorySummaryCreateRequestv2;
import io.github.nguyennhatquang.fashion.Inventory.delivery.Mapper.InventoryLedgerMapper;
import io.github.nguyennhatquang.fashion.Inventory.delivery.Mapper.WarehouseMapper;
import io.github.nguyennhatquang.fashion.Inventory.domain.IRepository.postgres.IInventoryLedgerRepository;
import io.github.nguyennhatquang.fashion.Inventory.domain.IRepository.postgres.IInventorySummaryRepository;
import io.github.nguyennhatquang.fashion.Inventory.domain.IRepository.postgres.IWarehouseRepository;
import io.github.nguyennhatquang.fashion.Inventory.domain.entity.InventoryLedger;
import io.github.nguyennhatquang.fashion.Inventory.domain.entity.Warehouse;
import io.github.nguyennhatquang.fashion.Inventory.usecase.IStrategy.IStrategyInventoryLedger;
import io.github.nguyennhatquang.fashion.Inventory.usecase.IUseCase.IAdminInventoryLedgerUseCase;
import io.github.nguyennhatquang.fashion.Inventory.usecase.Strategy.InventoryLedger.StrategyInventoryLedger;
import io.github.nguyennhatquang.fashion.common.request.ExactPageRequest;
import io.github.nguyennhatquang.fashion.common.response.ExactPageResponse;
import io.github.nguyennhatquang.fashion.common.response.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminInventoryLedgerUseCase implements IAdminInventoryLedgerUseCase {
    private final IInventoryLedgerRepository ledgerRepository;
    private final InventoryLedgerMapper ledgerMapper;
    private final IWarehouseRepository warehouseRepository;
    private final IInventorySummaryRepository summaryRepository;
    private final StrategyInventoryLedger strategyInventoryLedger;

    @Override
    public Result<InventoryLedger, Exception> createInventoryLedger(InventoryLedgerCreateRequest request) {
        try {
            if (request.warehouseId() == null) {
                return Result.error(new Exception("Warehouse ID is required"));
            }
            Warehouse warehouse = warehouseRepository.findById(request.warehouseId()).orElse(null);
            if (warehouse == null) {
                return Result.error(new Exception("Warehouse not found"));
            }
            InventoryLedger ledger = ledgerMapper.toEntity(request);
            ledger.setWarehouse(warehouse);
            ledgerRepository.save(ledger);
            InventorySummaryCreateRequestv2 summaryRequest = InventorySummaryCreateRequestv2.builder()
                    .warehouseId(request.warehouseId())
                    .skuCode(request.skuCode())
                    .onHand(request.quantityChange())
                    .reserved(0)
                    .build();
            IStrategyInventoryLedger strategy = strategyInventoryLedger.getStrategy(request.transactionType());
            Result<Void, Exception> result = strategy.execute(summaryRequest);
            if (result.hasError()) {
                return Result.error(new Exception(" InventorySummary failed"));
            }
            return Result.success(ledger);
        } catch (Exception e) {
            return Result.error(e);
        }
    }

    @Override
    public Result<InventoryLedger, Exception> getInventoryLedgerById(UUID id) {
        try {
            InventoryLedger ledger = ledgerRepository.findById(id).orElse(null);
            if (ledger == null) {
                return Result.error(new Exception("InventoryLedger not found"));
            }
            return Result.success(ledger);
        } catch (Exception e) {
            return Result.error(e);
        }
    }

    @Override
    public Result<ExactPageResponse<InventoryLedger>, Exception> getAllInventoryLedgers(ExactPageRequest request) {
        try {
            ExactPageResponse<InventoryLedger> ledgers = ledgerRepository.getInventoryLedgerExactPage(request);
            return Result.success(ledgers);
        } catch (Exception e) {
            return Result.error(e);
        }
    }
}
