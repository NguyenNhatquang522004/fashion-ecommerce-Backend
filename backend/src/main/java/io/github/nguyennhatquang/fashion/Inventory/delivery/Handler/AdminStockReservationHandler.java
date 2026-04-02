package io.github.nguyennhatquang.fashion.Inventory.delivery.Handler;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.nguyennhatquang.fashion.Inventory.delivery.Dto.StockReservation.StockReservationResponse;
import io.github.nguyennhatquang.fashion.Inventory.delivery.Dto.StockReservation.StockReservationRequest.StockReservationCreateRequest;
import io.github.nguyennhatquang.fashion.Inventory.delivery.Dto.StockReservation.StockReservationRequest.StockReservationUpdateRequest;
import io.github.nguyennhatquang.fashion.Inventory.delivery.Mapper.StockReservationMapper;
import io.github.nguyennhatquang.fashion.Inventory.domain.entity.StockReservation;
import io.github.nguyennhatquang.fashion.Inventory.usecase.IUseCase.IAdminStockReservationUseCase;
import io.github.nguyennhatquang.fashion.common.request.ExactPageRequestv2;
import io.github.nguyennhatquang.fashion.common.response.ExactPageResponse;
import io.github.nguyennhatquang.fashion.common.response.Result;
import io.github.nguyennhatquang.fashion.common.response.SystemRes;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/stock-reservations")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminStockReservationHandler {

    private final IAdminStockReservationUseCase adminStockReservationUseCase;
    private final StockReservationMapper stockReservationMapper;

    @PostMapping("/create")
    public ResponseEntity<SystemRes> createStockReservation(@Validated @RequestBody StockReservationCreateRequest request) {
        try {
            Result<StockReservation, Exception> result = adminStockReservationUseCase.createStockReservation(request);
            if (result.hasError()) {
                return ResponseEntity.badRequest().body(
                        SystemRes.builder().status("400").message(result.error().getMessage()).data(null).build());
            }
            StockReservationResponse response = stockReservationMapper.toResponse(result.data());
            return ResponseEntity.ok().body(
                    SystemRes.builder().status("200").message("Create stock reservation success").data(response).build());
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(SystemRes.builder().status("400").message(e.getMessage()).data(null).build());
        }
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<SystemRes> updateStockReservation(@PathVariable("id") UUID id,
            @Validated @RequestBody StockReservationUpdateRequest request) {
        try {
            Result<StockReservation, Exception> result = adminStockReservationUseCase.updateStockReservation(request, id);
            if (result.hasError()) {
                return ResponseEntity.badRequest().body(
                        SystemRes.builder().status("400").message(result.error().getMessage()).data(null).build());
            }
            StockReservationResponse response = stockReservationMapper.toResponse(result.data());
            return ResponseEntity.ok().body(
                    SystemRes.builder().status("200").message("Update stock reservation success").data(response).build());
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(SystemRes.builder().status("400").message(e.getMessage()).data(null).build());
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<SystemRes> deleteStockReservation(@PathVariable("id") UUID id) {
        try {
            Result<Void, Exception> result = adminStockReservationUseCase.deleteStockReservation(id);
            if (result.hasError()) {
                return ResponseEntity.badRequest().body(
                        SystemRes.builder().status("400").message(result.error().getMessage()).data(null).build());
            }
            return ResponseEntity.ok().body(
                    SystemRes.builder().status("200").message("Delete stock reservation success").data(null).build());
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(SystemRes.builder().status("400").message(e.getMessage()).data(null).build());
        }
    }

    @GetMapping("/get-detail/{id}")
    public ResponseEntity<SystemRes> getStockReservationById(@PathVariable("id") UUID id) {
        try {
            Result<StockReservation, Exception> result = adminStockReservationUseCase.getStockReservationById(id);
            if (result.hasError()) {
                return ResponseEntity.badRequest().body(
                        SystemRes.builder().status("400").message(result.error().getMessage()).data(null).build());
            }
            StockReservationResponse response = stockReservationMapper.toResponse(result.data());
            return ResponseEntity.ok().body(
                    SystemRes.builder().status("200").message("Get stock reservation success").data(response).build());
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(SystemRes.builder().status("400").message(e.getMessage()).data(null).build());
        }
    }

    @GetMapping("/get-page")
    public ResponseEntity<SystemRes> getPageStockReservation(@Validated ExactPageRequestv2 request) {
        try {
            Result<ExactPageResponse<StockReservation>, Exception> result = adminStockReservationUseCase.getAllStockReservations(request);
            if (result.hasError()) {
                return ResponseEntity.badRequest().body(
                        SystemRes.builder().status("400").message(result.error().getMessage()).data(null).build());
            }
            ExactPageResponse<StockReservation> page = result.data();
            ExactPageResponse<StockReservationResponse> responseData = ExactPageResponse.<StockReservationResponse>builder()
                    .currentPage(page.getCurrentPage())
                    .totalPages(page.getTotalPages())
                    .totalElements(page.getTotalElements())
                    .snapshotTime(page.getSnapshotTime())
                    .data(stockReservationMapper.toResponseList(page.getData()))
                    .build();
            return ResponseEntity.ok().body(
                    SystemRes.builder().status("200").message("Get page stock reservation success").data(responseData).build());
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(SystemRes.builder().status("400").message(e.getMessage()).data(null).build());
        }
    }
}
