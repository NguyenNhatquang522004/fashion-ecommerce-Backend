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

import io.github.nguyennhatquang.fashion.Inventory.delivery.Dto.OutboxEventInventory.OutboxEventInventoryResponse;
import io.github.nguyennhatquang.fashion.Inventory.delivery.Dto.OutboxEventInventory.OutboxEventInventoryRequest.OutboxEventInventoryCreateRequest;
import io.github.nguyennhatquang.fashion.Inventory.delivery.Dto.OutboxEventInventory.OutboxEventInventoryRequest.OutboxEventInventoryUpdateRequest;
import io.github.nguyennhatquang.fashion.Inventory.delivery.Mapper.OutboxEventInventoryMapper;
import io.github.nguyennhatquang.fashion.Inventory.domain.entity.OutboxEventInventory;
import io.github.nguyennhatquang.fashion.Inventory.usecase.IUseCase.IAdminOutboxEventInventoryUseCase;
import io.github.nguyennhatquang.fashion.common.request.ExactPageRequestv2;
import io.github.nguyennhatquang.fashion.common.response.ExactPageResponse;
import io.github.nguyennhatquang.fashion.common.response.Result;
import io.github.nguyennhatquang.fashion.common.response.SystemRes;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/outbox-event-inventory")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminOutboxEventInventoryHandler {

    private final IAdminOutboxEventInventoryUseCase adminOutboxEventInventoryUseCase;
    private final OutboxEventInventoryMapper outboxEventInventoryMapper;

    @PostMapping("/create")
    public ResponseEntity<SystemRes> createOutboxEvent(@Validated @RequestBody OutboxEventInventoryCreateRequest request) {
        try {
            Result<OutboxEventInventory, Exception> result = adminOutboxEventInventoryUseCase.createOutboxEvent(request);
            if (result.hasError()) {
                return ResponseEntity.badRequest().body(
                        SystemRes.builder().status("400").message(result.error().getMessage()).data(null).build());
            }
            OutboxEventInventoryResponse response = outboxEventInventoryMapper.toResponse(result.data());
            return ResponseEntity.ok().body(
                    SystemRes.builder().status("200").message("Create outbox event success").data(response).build());
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(SystemRes.builder().status("400").message(e.getMessage()).data(null).build());
        }
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<SystemRes> updateOutboxEventStatus(@PathVariable("id") UUID id,
            @Validated @RequestBody OutboxEventInventoryUpdateRequest request) {
        try {
            Result<OutboxEventInventory, Exception> result = adminOutboxEventInventoryUseCase.updateOutboxEventStatus(request, id);
            if (result.hasError()) {
                return ResponseEntity.badRequest().body(
                        SystemRes.builder().status("400").message(result.error().getMessage()).data(null).build());
            }
            OutboxEventInventoryResponse response = outboxEventInventoryMapper.toResponse(result.data());
            return ResponseEntity.ok().body(
                    SystemRes.builder().status("200").message("Update outbox event status success").data(response).build());
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(SystemRes.builder().status("400").message(e.getMessage()).data(null).build());
        }
    }

    @GetMapping("/get-detail/{id}")
    public ResponseEntity<SystemRes> getOutboxEventById(@PathVariable("id") UUID id) {
        try {
            Result<OutboxEventInventory, Exception> result = adminOutboxEventInventoryUseCase.getOutboxEventById(id);
            if (result.hasError()) {
                return ResponseEntity.badRequest().body(
                        SystemRes.builder().status("400").message(result.error().getMessage()).data(null).build());
            }
            OutboxEventInventoryResponse response = outboxEventInventoryMapper.toResponse(result.data());
            return ResponseEntity.ok().body(
                    SystemRes.builder().status("200").message("Get outbox event success").data(response).build());
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(SystemRes.builder().status("400").message(e.getMessage()).data(null).build());
        }
    }

    @GetMapping("/get-page")
    public ResponseEntity<SystemRes> getPageOutboxEvent(@Validated ExactPageRequestv2 request) {
        try {
            Result<ExactPageResponse<OutboxEventInventory>, Exception> result = adminOutboxEventInventoryUseCase.getAllOutboxEvents(request);
            if (result.hasError()) {
                return ResponseEntity.badRequest().body(
                        SystemRes.builder().status("400").message(result.error().getMessage()).data(null).build());
            }
            ExactPageResponse<OutboxEventInventory> page = result.data();
            ExactPageResponse<OutboxEventInventoryResponse> responseData = ExactPageResponse.<OutboxEventInventoryResponse>builder()
                    .currentPage(page.getCurrentPage())
                    .totalPages(page.getTotalPages())
                    .totalElements(page.getTotalElements())
                    .snapshotTime(page.getSnapshotTime())
                    .data(outboxEventInventoryMapper.toResponseList(page.getData()))
                    .build();
            return ResponseEntity.ok().body(
                    SystemRes.builder().status("200").message("Get page outbox event success").data(responseData).build());
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(SystemRes.builder().status("400").message(e.getMessage()).data(null).build());
        }
    }
}
