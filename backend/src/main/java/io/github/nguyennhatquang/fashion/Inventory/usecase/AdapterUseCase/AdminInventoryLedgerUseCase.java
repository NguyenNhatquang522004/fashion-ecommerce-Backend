package io.github.nguyennhatquang.fashion.Inventory.usecase.AdapterUseCase;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import io.github.nguyennhatquang.fashion.Inventory.delivery.Dto.InventoryLedger.InventoryLedgerRequest.InventoryLedgerCreateRequest;
import io.github.nguyennhatquang.fashion.Inventory.delivery.Mapper.InventoryLedgerMapper;
import io.github.nguyennhatquang.fashion.Inventory.domain.IRepository.postgres.IInventoryLedgerRepository;
import io.github.nguyennhatquang.fashion.Inventory.domain.entity.InventoryLedger;
import io.github.nguyennhatquang.fashion.Inventory.usecase.IUseCase.IAdminInventoryLedgerUseCase;
import io.github.nguyennhatquang.fashion.common.response.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminInventoryLedgerUseCase implements IAdminInventoryLedgerUseCase {
    private final IInventoryLedgerRepository ledgerRepository;
    private final InventoryLedgerMapper ledgerMapper;

    @Override
    public Result<InventoryLedger, Exception> createInventoryLedger(InventoryLedgerCreateRequest request) {
        try {
            InventoryLedger ledger = ledgerMapper.toEntity(request);
            ledgerRepository.save(ledger);
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
    public Result<List<InventoryLedger>, Exception> getAllInventoryLedgers() {
        try {
            List<InventoryLedger> ledgers = ledgerRepository.findAll();
            return Result.success(ledgers);
        } catch (Exception e) {
            return Result.error(e);
        }
    }
}
