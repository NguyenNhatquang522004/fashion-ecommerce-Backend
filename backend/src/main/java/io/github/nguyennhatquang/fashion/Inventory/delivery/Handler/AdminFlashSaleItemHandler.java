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

import io.github.nguyennhatquang.fashion.Inventory.delivery.Dto.FlashSaleItem.FlashSaleItemResponse;
import io.github.nguyennhatquang.fashion.Inventory.delivery.Dto.FlashSaleItem.FlashSaleItemRequest.FlashSaleItemCreateRequest;
import io.github.nguyennhatquang.fashion.Inventory.delivery.Dto.FlashSaleItem.FlashSaleItemRequest.FlashSaleItemUpdateRequest;
import io.github.nguyennhatquang.fashion.Inventory.delivery.Mapper.FlashSaleItemMapper;
import io.github.nguyennhatquang.fashion.Inventory.domain.entity.FlashSaleItem;
import io.github.nguyennhatquang.fashion.Inventory.usecase.IUseCase.IAdminFlashSaleItemUseCase;
import io.github.nguyennhatquang.fashion.common.request.ExactPageRequestv2;
import io.github.nguyennhatquang.fashion.common.response.ExactPageResponse;
import io.github.nguyennhatquang.fashion.common.response.Result;
import io.github.nguyennhatquang.fashion.common.response.SystemRes;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/flash-sale-items")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminFlashSaleItemHandler {

    private final IAdminFlashSaleItemUseCase adminFlashSaleItemUseCase;
    private final FlashSaleItemMapper flashSaleItemMapper;

    @PostMapping("/create")
    public ResponseEntity<SystemRes> createFlashSaleItem(@Validated @RequestBody FlashSaleItemCreateRequest request) {
        try {
            Result<FlashSaleItem, Exception> result = adminFlashSaleItemUseCase.createFlashSaleItem(request);
            if (result.hasError()) {
                return ResponseEntity.badRequest().body(
                        SystemRes.builder().status("400").message(result.error().getMessage()).data(null).build());
            }
            FlashSaleItemResponse response = flashSaleItemMapper.toResponse(result.data());
            return ResponseEntity.ok().body(
                    SystemRes.builder().status("200").message("Create flash sale item success").data(response).build());
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(SystemRes.builder().status("400").message(e.getMessage()).data(null).build());
        }
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<SystemRes> updateFlashSaleItem(@PathVariable("id") UUID id,
            @Validated @RequestBody FlashSaleItemUpdateRequest request) {
        try {
            Result<FlashSaleItem, Exception> result = adminFlashSaleItemUseCase.updateFlashSaleItem(request, id);
            if (result.hasError()) {
                return ResponseEntity.badRequest().body(
                        SystemRes.builder().status("400").message(result.error().getMessage()).data(null).build());
            }
            FlashSaleItemResponse response = flashSaleItemMapper.toResponse(result.data());
            return ResponseEntity.ok().body(
                    SystemRes.builder().status("200").message("Update flash sale item success").data(response).build());
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(SystemRes.builder().status("400").message(e.getMessage()).data(null).build());
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<SystemRes> deleteFlashSaleItem(@PathVariable("id") UUID id) {
        try {
            Result<Void, Exception> result = adminFlashSaleItemUseCase.deleteFlashSaleItem(id);
            if (result.hasError()) {
                return ResponseEntity.badRequest().body(
                        SystemRes.builder().status("400").message(result.error().getMessage()).data(null).build());
            }
            return ResponseEntity.ok().body(
                    SystemRes.builder().status("200").message("Delete flash sale item success").data(null).build());
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(SystemRes.builder().status("400").message(e.getMessage()).data(null).build());
        }
    }

    @GetMapping("/get-detail/{id}")
    public ResponseEntity<SystemRes> getFlashSaleItemById(@PathVariable("id") UUID id) {
        try {
            Result<FlashSaleItem, Exception> result = adminFlashSaleItemUseCase.getFlashSaleItemById(id);
            if (result.hasError()) {
                return ResponseEntity.badRequest().body(
                        SystemRes.builder().status("400").message(result.error().getMessage()).data(null).build());
            }
            FlashSaleItemResponse response = flashSaleItemMapper.toResponse(result.data());
            return ResponseEntity.ok().body(
                    SystemRes.builder().status("200").message("Get flash sale item success").data(response).build());
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(SystemRes.builder().status("400").message(e.getMessage()).data(null).build());
        }
    }

    @GetMapping("/get-page")
    public ResponseEntity<SystemRes> getPageFlashSaleItem(@Validated ExactPageRequestv2 request) {
        try {
            Result<ExactPageResponse<FlashSaleItem>, Exception> result = adminFlashSaleItemUseCase.getAllFlashSaleItems(request);
            if (result.hasError()) {
                return ResponseEntity.badRequest().body(
                        SystemRes.builder().status("400").message(result.error().getMessage()).data(null).build());
            }
            ExactPageResponse<FlashSaleItem> page = result.data();
            ExactPageResponse<FlashSaleItemResponse> responseData = ExactPageResponse.<FlashSaleItemResponse>builder()
                    .currentPage(page.getCurrentPage())
                    .totalPages(page.getTotalPages())
                    .totalElements(page.getTotalElements())
                    .snapshotTime(page.getSnapshotTime())
                    .data(flashSaleItemMapper.toResponseList(page.getData()))
                    .build();
            return ResponseEntity.ok().body(
                    SystemRes.builder().status("200").message("Get page flash sale item success").data(responseData).build());
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(SystemRes.builder().status("400").message(e.getMessage()).data(null).build());
        }
    }
}
