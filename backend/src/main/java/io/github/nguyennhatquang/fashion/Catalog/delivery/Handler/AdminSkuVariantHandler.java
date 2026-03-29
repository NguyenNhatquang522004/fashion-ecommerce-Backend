package io.github.nguyennhatquang.fashion.Catalog.delivery.Handler;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.nguyennhatquang.fashion.Catalog.delivery.Dto.SkuVariant.SkuVariantResponse;
import io.github.nguyennhatquang.fashion.Catalog.delivery.Dto.SkuVariant.SkuVariantRequest.SkuVariantCreateRequest;
import io.github.nguyennhatquang.fashion.Catalog.delivery.Dto.SkuVariant.SkuVariantRequest.SkuVariantUpdateRequest;
import io.github.nguyennhatquang.fashion.Catalog.delivery.Mapper.SkuVariantMapper;
import io.github.nguyennhatquang.fashion.Catalog.domain.entity.SkuVariant;
import io.github.nguyennhatquang.fashion.Catalog.usecase.IUseCase.IAdminSkuVariantUseCase;
import io.github.nguyennhatquang.fashion.common.request.ExactPageRequest;
import io.github.nguyennhatquang.fashion.common.request.PanigationRequest;
import io.github.nguyennhatquang.fashion.common.response.ExactPageResponse;
import io.github.nguyennhatquang.fashion.common.response.PanigationResponse;
import io.github.nguyennhatquang.fashion.common.response.Result;
import io.github.nguyennhatquang.fashion.common.response.SystemRes;
import lombok.RequiredArgsConstructor;

import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/admin/sku-variants")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminSkuVariantHandler {

    private final IAdminSkuVariantUseCase adminSkuVariantUseCase;
    private final SkuVariantMapper skuVariantMapper;

    @PostMapping("/create")
    public ResponseEntity<SystemRes> createSkuVariant(@Validated @RequestBody SkuVariantCreateRequest request) {
        try {
            Result<SkuVariant, Exception> result = adminSkuVariantUseCase.createSkuVariant(request);
            if (result.hasError()) {
                return ResponseEntity.badRequest().body(
                        SystemRes.builder().status("400").message(result.error().getMessage()).data(null).build());
            }
            SkuVariantResponse response = skuVariantMapper.toResponse(result.data());
            return ResponseEntity.ok().body(
                    SystemRes.builder().status("200").message("Create sku variant success").data(response).build());
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(SystemRes.builder().status("400").message(e.getMessage()).data(null).build());
        }
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<SystemRes> updateSkuVariant(@PathVariable("id") String id,
            @Validated @RequestBody SkuVariantUpdateRequest request) {
        try {
            Result<SkuVariant, Exception> result = adminSkuVariantUseCase.updateSkuVariant(id, request);
            if (result.hasError()) {
                return ResponseEntity.badRequest().body(
                        SystemRes.builder().status("400").message(result.error().getMessage()).data(null).build());
            }
            SkuVariantResponse response = skuVariantMapper.toResponse(result.data());
            return ResponseEntity.ok().body(
                    SystemRes.builder().status("200").message("Update sku variant success").data(response).build());
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(SystemRes.builder().status("400").message(e.getMessage()).data(null).build());
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<SystemRes> deleteSkuVariant(@PathVariable("id") String id) {
        try {
            Result<SkuVariant, Exception> result = adminSkuVariantUseCase.deleteSkuVariantById(id);
            if (result.hasError()) {
                return ResponseEntity.badRequest().body(
                        SystemRes.builder().status("400").message(result.error().getMessage()).data(null).build());
            }
            SkuVariantResponse response = skuVariantMapper.toResponse(result.data());
            return ResponseEntity.ok().body(
                    SystemRes.builder().status("200").message("Delete sku variant success").data(response).build());
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(SystemRes.builder().status("400").message(e.getMessage()).data(null).build());
        }
    }

    @DeleteMapping("/delete-by-product/{productId}")
    public ResponseEntity<SystemRes> deleteSkuVariantByProductId(@PathVariable("productId") String productId) {
        try {
            Result<Void, Exception> result = adminSkuVariantUseCase.deleteSkuVariantByProductId(productId);
            if (result.hasError()) {
                return ResponseEntity.badRequest().body(
                        SystemRes.builder().status("400").message(result.error().getMessage()).data(null).build());
            }
            return ResponseEntity.ok().body(
                    SystemRes.builder().status("200").message("Delete sku variants by product success").data(null)
                            .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(SystemRes.builder().status("400").message(e.getMessage()).data(null).build());
        }
    }

    @GetMapping("/get-detail/{id}")
    public ResponseEntity<SystemRes> getSkuVariantById(@PathVariable("id") String id) {
        try {
            Result<SkuVariant, Exception> result = adminSkuVariantUseCase.getSkuVariantById(id);
            if (result.hasError()) {
                return ResponseEntity.badRequest().body(
                        SystemRes.builder().status("400").message(result.error().getMessage()).data(null).build());
            }
            SkuVariantResponse response = skuVariantMapper.toResponse(result.data());
            return ResponseEntity.ok().body(
                    SystemRes.builder().status("200").message("Get sku variant success").data(response).build());
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(SystemRes.builder().status("400").message(e.getMessage()).data(null).build());
        }
    }

    @GetMapping("/get-by-product/{productId}")
    public ResponseEntity<SystemRes> getSkuVariantByProductId(@PathVariable("productId") String productId) {
        try {
            Result<List<SkuVariant>, Exception> result = adminSkuVariantUseCase.getSkuVariantByProductId(productId);
            if (result.hasError()) {
                return ResponseEntity.badRequest().body(
                        SystemRes.builder().status("400").message(result.error().getMessage()).data(null).build());
            }
            List<SkuVariantResponse> response = skuVariantMapper.toResponseList(result.data());
            return ResponseEntity.ok().body(
                    SystemRes.builder().status("200").message("Get sku variants by product success").data(response)
                            .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(SystemRes.builder().status("400").message(e.getMessage()).data(null).build());
        }
    }

    @GetMapping("/get-page")
    public ResponseEntity<SystemRes> getPageSkuVariant(@Validated ExactPageRequest request) {
        try {
            Result<ExactPageResponse<SkuVariant>, Exception> result = adminSkuVariantUseCase
                    .GetExactPageResponse(request);
            if (result.hasError()) {
                return ResponseEntity.badRequest().body(
                        SystemRes.builder().status("400").message(result.error().getMessage()).data(null).build());
            }
            ExactPageResponse<SkuVariant> page = result.data();
            ExactPageResponse<SkuVariantResponse> responseData = ExactPageResponse.<SkuVariantResponse>builder()
                    .currentPage(page.getCurrentPage())
                    .totalPages(page.getTotalPages())
                    .totalElements(page.getTotalElements())
                    .snapshotTime(page.getSnapshotTime())
                    .data(skuVariantMapper.toResponseList(page.getData()))
                    .build();
            return ResponseEntity.ok().body(
                    SystemRes.builder().status("200").message("Get page sku variant success").data(responseData)
                            .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(SystemRes.builder().status("400").message(e.getMessage()).data(null).build());
        }
    }

    @GetMapping("/get-cursor")
    public ResponseEntity<SystemRes> getSkuVariantCursor(@Validated PanigationRequest request) {
        try {
            Result<PanigationResponse<SkuVariant>, Exception> result = adminSkuVariantUseCase
                    .getBrandsCursor(request);
            if (result.hasError()) {
                return ResponseEntity.badRequest().body(
                        SystemRes.builder().status("400").message(result.error().getMessage()).data(null).build());
            }
            PanigationResponse<SkuVariant> page = result.data();
            PanigationResponse<SkuVariantResponse> responseData = PanigationResponse.<SkuVariantResponse>builder()
                    .cursor(page.getCursor())
                    .limit(page.getLimit())
                    .sort(page.getSort())
                    .hasNext(page.getHasNext())
                    .hasPrevious(page.getHasPrevious())
                    .data(skuVariantMapper.toResponseList(page.getData()))
                    .build();
            return ResponseEntity.ok().body(
                    SystemRes.builder().status("200").message("Get cursor sku variant success").data(responseData)
                            .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(SystemRes.builder().status("400").message(e.getMessage()).data(null).build());
        }
    }
}
