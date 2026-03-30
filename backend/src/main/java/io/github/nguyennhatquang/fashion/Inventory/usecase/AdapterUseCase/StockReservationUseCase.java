package io.github.nguyennhatquang.fashion.Inventory.usecase.AdapterUseCase;

import java.util.Optional;

import org.springframework.stereotype.Service;

import io.github.nguyennhatquang.fashion.Inventory.delivery.Dto.InventoryLedger.InventoryLedgerRequest.InventoryLedgerCreateRequest;
import io.github.nguyennhatquang.fashion.Inventory.delivery.Dto.StockReservation.StockReservationRequest.StockReservationCreateRequest;
import io.github.nguyennhatquang.fashion.Inventory.delivery.Mapper.StockReservationMapper;
import io.github.nguyennhatquang.fashion.Inventory.domain.IRepository.postgres.IStockReservationRepository;
import io.github.nguyennhatquang.fashion.Inventory.domain.entity.StockReservation;
import io.github.nguyennhatquang.fashion.Inventory.usecase.IUseCase.IStockReservationUseCase;
import io.github.nguyennhatquang.fashion.common.Enum.InventoryTransactionTypeEnum;
import io.github.nguyennhatquang.fashion.common.response.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class StockReservationUseCase implements IStockReservationUseCase {
    private final IStockReservationRepository stockReservationRepository;
    private final StockReservationMapper stockReservationMapper;

    @Override
    public Result<StockReservation, Exception> createStockReservation(StockReservationCreateRequest request) {
        try {
            Optional<StockReservation> existingReservation = stockReservationRepository
                    .findByOrderIdAndSkuCodeAndWarehouseId(request.orderId(), request.skuCode(), request.warehouseId());
            if (existingReservation.isPresent()) {
                return Result.error(new Exception("Stock reservation already exists for order " + request.orderId()));
            }
            StockReservation stockReservation = stockReservationMapper.toEntity(request);
            stockReservationRepository.save(stockReservation);
            InventoryLedgerCreateRequest ledgerRequest = InventoryLedgerCreateRequest.builder()
                    .skuCode(request.skuCode())
                    .warehouseId(request.warehouseId())
                    .quantityChange(-request.quantity())
                    .transactionType(InventoryTransactionTypeEnum.RESERVE)
                    .referenceId(request.orderId())
                    .note("Reservation for order " + request.orderId())
                    .build();
            return Result.success(stockReservation);
        } catch (Exception e) {
            return Result.error(e);
        }
    }
}
