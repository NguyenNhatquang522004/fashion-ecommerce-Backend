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

import io.github.nguyennhatquang.fashion.Inventory.delivery.Dto.InventorySummary.InventorySummaryResponse;
import io.github.nguyennhatquang.fashion.Inventory.delivery.Dto.InventorySummary.InventorySummaryRequest.InventorySummaryCreateRequest;
import io.github.nguyennhatquang.fashion.Inventory.delivery.Dto.InventorySummary.InventorySummaryRequest.InventorySummaryUpdateRequest;
import io.github.nguyennhatquang.fashion.Inventory.delivery.Mapper.InventorySummaryMapper;
import io.github.nguyennhatquang.fashion.Inventory.domain.entity.InventorySummary;
import io.github.nguyennhatquang.fashion.Inventory.usecase.IUseCase.IAdminInventorySummaryUseCase;
import io.github.nguyennhatquang.fashion.common.request.ExactPageRequestv2;
import io.github.nguyennhatquang.fashion.common.response.ExactPageResponse;
import io.github.nguyennhatquang.fashion.common.response.Result;
import io.github.nguyennhatquang.fashion.common.response.SystemRes;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/inventory-summaries")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminInventorySummaryHandler {

    private final IAdminInventorySummaryUseCase adminInventorySummaryUseCase;
    private final InventorySummaryMapper inventorySummaryMapper;

    @PostMapping("/create")
    public ResponseEntity<SystemRes> createInventorySummary(@Validated @RequestBody InventorySummaryCreateRequest request) {
        try {
            Result<InventorySummary, Exception> result = adminInventorySummaryUseCase.createInventorySummary(request);
            if (result.hasError()) {
                return ResponseEntity.badRequest().body(
                        SystemRes.builder().status("400").message(result.error().getMessage()).data(null).build());
            }
            InventorySummaryResponse response = inventorySummaryMapper.toResponse(result.data());
            return ResponseEntity.ok().body(
                    SystemRes.builder().status("200").message("Create inventory summary success").data(response).build());
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(SystemRes.builder().status("400").message(e.getMessage()).data(null).build());
        }
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<SystemRes> updateInventorySummary(@PathVariable("id") UUID id,
            @Validated @RequestBody InventorySummaryUpdateRequest request) {
        try {
            Result<InventorySummary, Exception> result = adminInventorySummaryUseCase.updateInventorySummary(request, id);
            if (result.hasError()) {
                return ResponseEntity.badRequest().body(
                        SystemRes.builder().status("400").message(result.error().getMessage()).data(null).build());
            }
            InventorySummaryResponse response = inventorySummaryMapper.toResponse(result.data());
            return ResponseEntity.ok().body(
                    SystemRes.builder().status("200").message("Update inventory summary success").data(response).build());
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(SystemRes.builder().status("400").message(e.getMessage()).data(null).build());
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<SystemRes> deleteInventorySummary(@PathVariable("id") UUID id) {
        try {
            Result<Void, Exception> result = adminInventorySummaryUseCase.deleteInventorySummary(id);
            if (result.hasError()) {
                return ResponseEntity.badRequest().body(
                        SystemRes.builder().status("400").message(result.error().getMessage()).data(null).build());
            }
            return ResponseEntity.ok().body(
                    SystemRes.builder().status("200").message("Delete inventory summary success").data(null).build());
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(SystemRes.builder().status("400").message(e.getMessage()).data(null).build());
        }
    }

    @GetMapping("/get-detail/{id}")
    public ResponseEntity<SystemRes> getInventorySummaryById(@PathVariable("id") UUID id) {
        try {
            Result<InventorySummary, Exception> result = adminInventorySummaryUseCase.getInventorySummaryById(id);
            if (result.hasError()) {
                return ResponseEntity.badRequest().body(
                        SystemRes.builder().status("400").message(result.error().getMessage()).data(null).build());
            }
            InventorySummaryResponse response = inventorySummaryMapper.toResponse(result.data());
            return ResponseEntity.ok().body(
                    SystemRes.builder().status("200").message("Get inventory summary success").data(response).build());
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(SystemRes.builder().status("400").message(e.getMessage()).data(null).build());
        }
    }

    @GetMapping("/get-page")
    public ResponseEntity<SystemRes> getPageInventorySummary(@Validated ExactPageRequestv2 request) {
        try {
            Result<ExactPageResponse<InventorySummary>, Exception> result = adminInventorySummaryUseCase.getAllInventorySummaries(request);
            if (result.hasError()) {
                return ResponseEntity.badRequest().body(
                        SystemRes.builder().status("400").message(result.error().getMessage()).data(null).build());
            }
            ExactPageResponse<InventorySummary> page = result.data();
            ExactPageResponse<InventorySummaryResponse> responseData = ExactPageResponse.<InventorySummaryResponse>builder()
                    .currentPage(page.getCurrentPage())
                    .totalPages(page.getTotalPages())
                    .totalElements(page.getTotalElements())
                    .snapshotTime(page.getSnapshotTime())
                    .data(inventorySummaryMapper.toResponseList(page.getData()))
                    .build();
            return ResponseEntity.ok().body(
                    SystemRes.builder().status("200").message("Get page inventory summary success").data(responseData).build());
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(SystemRes.builder().status("400").message(e.getMessage()).data(null).build());
        }
    }
}
