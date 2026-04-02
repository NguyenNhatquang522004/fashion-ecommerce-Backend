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

import io.github.nguyennhatquang.fashion.Inventory.delivery.Dto.StockLocation.StockLocationResponse;
import io.github.nguyennhatquang.fashion.Inventory.delivery.Dto.StockLocation.StockLocationRequest.StockLocationCreateRequest;
import io.github.nguyennhatquang.fashion.Inventory.delivery.Dto.StockLocation.StockLocationRequest.StockLocationUpdateRequest;
import io.github.nguyennhatquang.fashion.Inventory.delivery.Mapper.StockLocationMapper;
import io.github.nguyennhatquang.fashion.Inventory.domain.entity.StockLocation;
import io.github.nguyennhatquang.fashion.Inventory.usecase.IUseCase.IAdminStockLocationUseCase;
import io.github.nguyennhatquang.fashion.common.request.ExactPageRequestv2;
import io.github.nguyennhatquang.fashion.common.response.ExactPageResponse;
import io.github.nguyennhatquang.fashion.common.response.Result;
import io.github.nguyennhatquang.fashion.common.response.SystemRes;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/stock-locations")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminStockLocationHandler {

    private final IAdminStockLocationUseCase adminStockLocationUseCase;
    private final StockLocationMapper stockLocationMapper;

    @PostMapping("/create")
    public ResponseEntity<SystemRes> createStockLocation(@Validated @RequestBody StockLocationCreateRequest request) {
        try {
            Result<StockLocation, Exception> result = adminStockLocationUseCase.createStockLocation(request);
            if (result.hasError()) {
                return ResponseEntity.badRequest().body(
                        SystemRes.builder().status("400").message(result.error().getMessage()).data(null).build());
            }
            StockLocationResponse response = stockLocationMapper.toResponse(result.data());
            return ResponseEntity.ok().body(
                    SystemRes.builder().status("200").message("Create stock location success").data(response).build());
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(SystemRes.builder().status("400").message(e.getMessage()).data(null).build());
        }
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<SystemRes> updateStockLocation(@PathVariable("id") UUID id,
            @Validated @RequestBody StockLocationUpdateRequest request) {
        try {
            Result<StockLocation, Exception> result = adminStockLocationUseCase.updateStockLocation(request, id);
            if (result.hasError()) {
                return ResponseEntity.badRequest().body(
                        SystemRes.builder().status("400").message(result.error().getMessage()).data(null).build());
            }
            StockLocationResponse response = stockLocationMapper.toResponse(result.data());
            return ResponseEntity.ok().body(
                    SystemRes.builder().status("200").message("Update stock location success").data(response).build());
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(SystemRes.builder().status("400").message(e.getMessage()).data(null).build());
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<SystemRes> deleteStockLocation(@PathVariable("id") UUID id) {
        try {
            Result<Void, Exception> result = adminStockLocationUseCase.deleteStockLocation(id);
            if (result.hasError()) {
                return ResponseEntity.badRequest().body(
                        SystemRes.builder().status("400").message(result.error().getMessage()).data(null).build());
            }
            return ResponseEntity.ok().body(
                    SystemRes.builder().status("200").message("Delete stock location success").data(null).build());
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(SystemRes.builder().status("400").message(e.getMessage()).data(null).build());
        }
    }

    @GetMapping("/get-detail/{id}")
    public ResponseEntity<SystemRes> getStockLocationById(@PathVariable("id") UUID id) {
        try {
            Result<StockLocation, Exception> result = adminStockLocationUseCase.getStockLocationById(id);
            if (result.hasError()) {
                return ResponseEntity.badRequest().body(
                        SystemRes.builder().status("400").message(result.error().getMessage()).data(null).build());
            }
            StockLocationResponse response = stockLocationMapper.toResponse(result.data());
            return ResponseEntity.ok().body(
                    SystemRes.builder().status("200").message("Get stock location success").data(response).build());
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(SystemRes.builder().status("400").message(e.getMessage()).data(null).build());
        }
    }

    @GetMapping("/get-page")
    public ResponseEntity<SystemRes> getPageStockLocation(@Validated ExactPageRequestv2 request) {
        try {
            Result<ExactPageResponse<StockLocation>, Exception> result = adminStockLocationUseCase.getAllStockLocations(request);
            if (result.hasError()) {
                return ResponseEntity.badRequest().body(
                        SystemRes.builder().status("400").message(result.error().getMessage()).data(null).build());
            }
            ExactPageResponse<StockLocation> page = result.data();
            ExactPageResponse<StockLocationResponse> responseData = ExactPageResponse.<StockLocationResponse>builder()
                    .currentPage(page.getCurrentPage())
                    .totalPages(page.getTotalPages())
                    .totalElements(page.getTotalElements())
                    .snapshotTime(page.getSnapshotTime())
                    .data(stockLocationMapper.toResponseList(page.getData()))
                    .build();
            return ResponseEntity.ok().body(
                    SystemRes.builder().status("200").message("Get page stock location success").data(responseData).build());
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(SystemRes.builder().status("400").message(e.getMessage()).data(null).build());
        }
    }
}
