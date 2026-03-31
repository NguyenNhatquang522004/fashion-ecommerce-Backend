package io.github.nguyennhatquang.fashion.Inventory.delivery.Handler;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.nguyennhatquang.fashion.Inventory.delivery.Dto.StockReservation.StockReservationResponse;
import io.github.nguyennhatquang.fashion.Inventory.delivery.Mapper.StockReservationMapper;
import io.github.nguyennhatquang.fashion.Inventory.domain.entity.StockReservation;
import io.github.nguyennhatquang.fashion.Inventory.usecase.IUseCase.IStockReservationUseCase;
import io.github.nguyennhatquang.fashion.common.Payload.inventory.StockReservationCreatePayload;
import io.github.nguyennhatquang.fashion.common.response.Result;
import io.github.nguyennhatquang.fashion.common.response.SystemRes;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/stock-reservations")
@RequiredArgsConstructor
public class StockReservationHandler {

    private final IStockReservationUseCase stockReservationUseCase;
    private final StockReservationMapper stockReservationMapper;

    @PostMapping("/create-or-update")
    public ResponseEntity<SystemRes> createOrUpdateStockReservation(@Validated @RequestBody StockReservationCreatePayload request) {
        try {
            Result<StockReservation, Exception> result = stockReservationUseCase.createOrUpdateStockReservation(request);
            if (result.hasError()) {
                return ResponseEntity.badRequest().body(
                        SystemRes.builder().status("400").message(result.error().getMessage()).data(null).build());
            }
            StockReservationResponse response = stockReservationMapper.toResponse(result.data());
            return ResponseEntity.ok().body(
                    SystemRes.builder().status("200").message("Create or update stock reservation success").data(response).build());
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(SystemRes.builder().status("400").message(e.getMessage()).data(null).build());
        }
    }
}
