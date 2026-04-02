package io.github.nguyennhatquang.fashion.Inventory.delivery.Handler;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.nguyennhatquang.fashion.Inventory.delivery.Dto.InventoryLedger.InventoryLedgerResponse;
import io.github.nguyennhatquang.fashion.Inventory.delivery.Dto.InventoryLedger.InventoryLedgerRequest.InventoryLedgerCreateRequest;
import io.github.nguyennhatquang.fashion.Inventory.delivery.Mapper.InventoryLedgerMapper;
import io.github.nguyennhatquang.fashion.Inventory.domain.entity.InventoryLedger;
import io.github.nguyennhatquang.fashion.Inventory.usecase.IUseCase.IAdminInventoryLedgerUseCase;
import io.github.nguyennhatquang.fashion.common.request.ExactPageRequestv2;
import io.github.nguyennhatquang.fashion.common.response.ExactPageResponse;
import io.github.nguyennhatquang.fashion.common.response.Result;
import io.github.nguyennhatquang.fashion.common.response.SystemRes;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/inventory-ledgers")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminInventoryLedgerHandler {

    private final IAdminInventoryLedgerUseCase adminInventoryLedgerUseCase;
    private final InventoryLedgerMapper inventoryLedgerMapper;

    @PostMapping("/create")
    public ResponseEntity<SystemRes> createInventoryLedger(@Validated @RequestBody InventoryLedgerCreateRequest request) {
        try {
            Result<InventoryLedger, Exception> result = adminInventoryLedgerUseCase.createInventoryLedger(request);
            if (result.hasError()) {
                return ResponseEntity.badRequest().body(
                        SystemRes.builder().status("400").message(result.error().getMessage()).data(null).build());
            }
            InventoryLedgerResponse response = inventoryLedgerMapper.toResponse(result.data());
            return ResponseEntity.ok().body(
                    SystemRes.builder().status("200").message("Create inventory ledger success").data(response).build());
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(SystemRes.builder().status("400").message(e.getMessage()).data(null).build());
        }
    }

    @GetMapping("/get-detail/{id}")
    public ResponseEntity<SystemRes> getInventoryLedgerById(@PathVariable("id") UUID id) {
        try {
            Result<InventoryLedger, Exception> result = adminInventoryLedgerUseCase.getInventoryLedgerById(id);
            if (result.hasError()) {
                return ResponseEntity.badRequest().body(
                        SystemRes.builder().status("400").message(result.error().getMessage()).data(null).build());
            }
            InventoryLedgerResponse response = inventoryLedgerMapper.toResponse(result.data());
            return ResponseEntity.ok().body(
                    SystemRes.builder().status("200").message("Get inventory ledger success").data(response).build());
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(SystemRes.builder().status("400").message(e.getMessage()).data(null).build());
        }
    }

    @GetMapping("/get-page")
    public ResponseEntity<SystemRes> getPageInventoryLedger(@Validated ExactPageRequestv2 request) {
        try {
            Result<ExactPageResponse<InventoryLedger>, Exception> result = adminInventoryLedgerUseCase.getAllInventoryLedgers(request);
            if (result.hasError()) {
                return ResponseEntity.badRequest().body(
                        SystemRes.builder().status("400").message(result.error().getMessage()).data(null).build());
            }
            ExactPageResponse<InventoryLedger> page = result.data();
            ExactPageResponse<InventoryLedgerResponse> responseData = ExactPageResponse.<InventoryLedgerResponse>builder()
                    .currentPage(page.getCurrentPage())
                    .totalPages(page.getTotalPages())
                    .totalElements(page.getTotalElements())
                    .snapshotTime(page.getSnapshotTime())
                    .data(inventoryLedgerMapper.toResponseList(page.getData()))
                    .build();
            return ResponseEntity.ok().body(
                    SystemRes.builder().status("200").message("Get page inventory ledger success").data(responseData).build());
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(SystemRes.builder().status("400").message(e.getMessage()).data(null).build());
        }
    }
}
