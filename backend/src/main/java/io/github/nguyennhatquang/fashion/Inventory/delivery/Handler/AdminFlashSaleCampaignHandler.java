package io.github.nguyennhatquang.fashion.Inventory.delivery.Handler;

import java.util.List;
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

import io.github.nguyennhatquang.fashion.Inventory.delivery.Dto.FlashSaleCampaign.FlashSaleCampaignResponse;
import io.github.nguyennhatquang.fashion.Inventory.delivery.Dto.FlashSaleCampaign.FlashSaleCampaignRequest.FlashSaleCampaignCreateRequest;
import io.github.nguyennhatquang.fashion.Inventory.delivery.Dto.FlashSaleCampaign.FlashSaleCampaignRequest.FlashSaleCampaignUpdateRequest;
import io.github.nguyennhatquang.fashion.Inventory.delivery.Dto.FlashSaleItem.FlashSaleItemRequest.FlashSaleItemCreateRequest;
import io.github.nguyennhatquang.fashion.Inventory.delivery.Mapper.FlashSaleCampaignMapper;
import io.github.nguyennhatquang.fashion.Inventory.domain.entity.FlashSaleCampaign;
import io.github.nguyennhatquang.fashion.Inventory.usecase.IUseCase.IAdminFlashSaleCampaignUseCase;
import io.github.nguyennhatquang.fashion.common.request.ExactPageRequestv2;
import io.github.nguyennhatquang.fashion.common.response.ExactPageResponse;
import io.github.nguyennhatquang.fashion.common.response.Result;
import io.github.nguyennhatquang.fashion.common.response.SystemRes;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/flash-sale-campaigns")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminFlashSaleCampaignHandler {

    private final IAdminFlashSaleCampaignUseCase adminFlashSaleCampaignUseCase;
    private final FlashSaleCampaignMapper flashSaleCampaignMapper;

    @PostMapping("/create")
    public ResponseEntity<SystemRes> createCampaign(@Validated @RequestBody FlashSaleCampaignCreateRequest request,
            @Validated @RequestBody List<FlashSaleItemCreateRequest> requestFlashSaleItem) {
        try {
            Result<FlashSaleCampaign, Exception> result = adminFlashSaleCampaignUseCase.createCampaign(request,
                    requestFlashSaleItem);
            if (result.hasError()) {
                return ResponseEntity.badRequest().body(
                        SystemRes.builder().status("400").message(result.error().getMessage()).data(null).build());
            }
            FlashSaleCampaignResponse response = flashSaleCampaignMapper.toResponse(result.data());
            return ResponseEntity.ok().body(
                    SystemRes.builder().status("200").message("Create campaign success").data(response).build());
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(SystemRes.builder().status("400").message(e.getMessage()).data(null).build());
        }
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<SystemRes> updateCampaign(@PathVariable("id") UUID id,
            @Validated @RequestBody FlashSaleCampaignUpdateRequest request) {
        try {
            Result<FlashSaleCampaign, Exception> result = adminFlashSaleCampaignUseCase.updateCampaign(request, id);
            if (result.hasError()) {
                return ResponseEntity.badRequest().body(
                        SystemRes.builder().status("400").message(result.error().getMessage()).data(null).build());
            }
            FlashSaleCampaignResponse response = flashSaleCampaignMapper.toResponse(result.data());
            return ResponseEntity.ok().body(
                    SystemRes.builder().status("200").message("Update campaign success").data(response).build());
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(SystemRes.builder().status("400").message(e.getMessage()).data(null).build());
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<SystemRes> deleteCampaign(@PathVariable("id") UUID id) {
        try {
            Result<Void, Exception> result = adminFlashSaleCampaignUseCase.deleteCampaign(id);
            if (result.hasError()) {
                return ResponseEntity.badRequest().body(
                        SystemRes.builder().status("400").message(result.error().getMessage()).data(null).build());
            }
            return ResponseEntity.ok().body(
                    SystemRes.builder().status("200").message("Delete campaign success").data(null).build());
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(SystemRes.builder().status("400").message(e.getMessage()).data(null).build());
        }
    }

    @GetMapping("/get-detail/{id}")
    public ResponseEntity<SystemRes> getCampaignById(@PathVariable("id") UUID id) {
        try {
            Result<FlashSaleCampaign, Exception> result = adminFlashSaleCampaignUseCase.getCampaignById(id);
            if (result.hasError()) {
                return ResponseEntity.badRequest().body(
                        SystemRes.builder().status("400").message(result.error().getMessage()).data(null).build());
            }
            FlashSaleCampaignResponse response = flashSaleCampaignMapper.toResponse(result.data());
            return ResponseEntity.ok().body(
                    SystemRes.builder().status("200").message("Get campaign success").data(response).build());
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(SystemRes.builder().status("400").message(e.getMessage()).data(null).build());
        }
    }

    @GetMapping("/get-page")
    public ResponseEntity<SystemRes> getPageCampaign(@Validated ExactPageRequestv2 request) {
        try {
            Result<ExactPageResponse<FlashSaleCampaign>, Exception> result = adminFlashSaleCampaignUseCase
                    .getAllCampaigns(request);
            if (result.hasError()) {
                return ResponseEntity.badRequest().body(
                        SystemRes.builder().status("400").message(result.error().getMessage()).data(null).build());
            }
            ExactPageResponse<FlashSaleCampaign> page = result.data();
            ExactPageResponse<FlashSaleCampaignResponse> responseData = ExactPageResponse
                    .<FlashSaleCampaignResponse>builder()
                    .currentPage(page.getCurrentPage())
                    .totalPages(page.getTotalPages())
                    .totalElements(page.getTotalElements())
                    .snapshotTime(page.getSnapshotTime())
                    .data(flashSaleCampaignMapper.toResponseList(page.getData()))
                    .build();
            return ResponseEntity.ok().body(
                    SystemRes.builder().status("200").message("Get page campaign success").data(responseData).build());
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(SystemRes.builder().status("400").message(e.getMessage()).data(null).build());
        }
    }
}
