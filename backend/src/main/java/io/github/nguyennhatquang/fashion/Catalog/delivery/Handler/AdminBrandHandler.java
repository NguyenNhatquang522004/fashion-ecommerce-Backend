package io.github.nguyennhatquang.fashion.Catalog.delivery.Handler;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.nguyennhatquang.fashion.Catalog.delivery.Dto.Brand.BrandResponse;
import io.github.nguyennhatquang.fashion.Catalog.delivery.Dto.Brand.BrandRequest.BrandCreateRequest;
import io.github.nguyennhatquang.fashion.Catalog.delivery.Dto.Brand.BrandRequest.BrandUpdateRequest;
import io.github.nguyennhatquang.fashion.Catalog.delivery.Mapper.BrandMapper;
import io.github.nguyennhatquang.fashion.Catalog.domain.entity.Brand;
import io.github.nguyennhatquang.fashion.Catalog.usecase.IUseCase.IAdminBrandUseCase;
import io.github.nguyennhatquang.fashion.common.kafka.EventContext;
import io.github.nguyennhatquang.fashion.common.logs.MdcLog;
import io.github.nguyennhatquang.fashion.common.request.ExactPageRequest;
import io.github.nguyennhatquang.fashion.common.request.PanigationRequest;
import io.github.nguyennhatquang.fashion.common.response.ExactPageResponse;
import io.github.nguyennhatquang.fashion.common.response.PanigationResponse;
import io.github.nguyennhatquang.fashion.common.response.Result;
import io.github.nguyennhatquang.fashion.common.response.SystemRes;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/brands")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminBrandHandler {

    private final IAdminBrandUseCase adminBrandUseCase;
    private final BrandMapper brandMapper;

    @PostMapping("/create")
    public ResponseEntity<SystemRes> createBrand(@Validated @RequestBody BrandCreateRequest request) {
        try {
            Result<Brand, Exception> result = adminBrandUseCase.createBrand(request);
            if (result.hasError()) {
                return ResponseEntity.badRequest().body(
                        SystemRes.builder().status("400").message(result.error().getMessage()).data(null).build());
            }
            BrandResponse response = brandMapper.toResponse(result.data());
            return ResponseEntity.ok().body(
                    SystemRes.builder().status("200").message("Create brand success").data(response).build());
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(SystemRes.builder().status("400").message(e.getMessage()).data(null).build());
        }
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<SystemRes> updateBrand(@PathVariable("id") String id,
            @Validated @RequestBody BrandUpdateRequest request) {
        try {
            Result<Brand, Exception> result = adminBrandUseCase.updateBrand(request, id);
            if (result.hasError()) {
                return ResponseEntity.badRequest().body(
                        SystemRes.builder().status("400").message(result.error().getMessage()).data(null).build());
            }
            BrandResponse response = brandMapper.toResponse(result.data());
            return ResponseEntity.ok().body(
                    SystemRes.builder().status("200").message("Update brand success").data(response).build());
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(SystemRes.builder().status("400").message(e.getMessage()).data(null).build());
        }
    }

    @MdcLog
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<SystemRes> deleteBrand(@PathVariable("id") String id,
            @AuthenticationPrincipal Jwt jwt,
            @RequestHeader(value = "X-Correlation-ID", required = false) String incomingCorrelationId) {
        String userId = jwt.getClaimAsString("userId");
        EventContext ctx;
        if (incomingCorrelationId != null && !incomingCorrelationId.isBlank()) {
            ctx = new EventContext(incomingCorrelationId, userId, incomingCorrelationId,
                    System.currentTimeMillis() + 5000L);
        } else {
            ctx = EventContext.generate(userId);
        }
        try {
            Result<Void, Exception> result = adminBrandUseCase.deleteBrand(ctx, id);
            if (result.hasError()) {
                return ResponseEntity.badRequest().body(
                        SystemRes.builder().status("400").message(result.error().getMessage()).data(null).build());
            }
            return ResponseEntity.ok().body(
                    SystemRes.builder().status("200").message("Delete brand success").data(null).build());
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(SystemRes.builder().status("400").message(e.getMessage()).data(null).build());
        }
    }

    @GetMapping("/get-detail/{id}")
    public ResponseEntity<SystemRes> getBrandById(@PathVariable("id") String id) {
        try {
            Result<Brand, Exception> result = adminBrandUseCase.getBrandById(id);
            if (result.hasError()) {
                return ResponseEntity.badRequest().body(
                        SystemRes.builder().status("400").message(result.error().getMessage()).data(null).build());
            }
            BrandResponse response = brandMapper.toResponse(result.data());
            return ResponseEntity.ok().body(
                    SystemRes.builder().status("200").message("Get brand success").data(response).build());
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(SystemRes.builder().status("400").message(e.getMessage()).data(null).build());
        }
    }

    @GetMapping("/get-all")
    public ResponseEntity<SystemRes> getAllBrand() {
        try {
            Result<List<Brand>, Exception> result = adminBrandUseCase.getAllBrand();
            if (result.hasError()) {
                return ResponseEntity.badRequest().body(
                        SystemRes.builder().status("400").message(result.error().getMessage()).data(null).build());
            }
            List<BrandResponse> response = brandMapper.toResponseList(result.data());
            return ResponseEntity.ok().body(
                    SystemRes.builder().status("200").message("Get all brand success").data(response).build());
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(SystemRes.builder().status("400").message(e.getMessage()).data(null).build());
        }
    }

    @GetMapping("/get-page")
    public ResponseEntity<SystemRes> getPageBrand(@Validated ExactPageRequest request) {
        try {
            Result<ExactPageResponse<Brand>, Exception> result = adminBrandUseCase.GetExactPageResponse(request);
            if (result.hasError()) {
                return ResponseEntity.badRequest().body(
                        SystemRes.builder().status("400").message(result.error().getMessage()).data(null).build());
            }
            ExactPageResponse<Brand> page = result.data();
            ExactPageResponse<BrandResponse> responseData = ExactPageResponse.<BrandResponse>builder()
                    .currentPage(page.getCurrentPage())
                    .totalPages(page.getTotalPages())
                    .totalElements(page.getTotalElements())
                    .snapshotTime(page.getSnapshotTime())
                    .data(brandMapper.toResponseList(page.getData()))
                    .build();
            return ResponseEntity.ok().body(
                    SystemRes.builder().status("200").message("Get page brand success").data(responseData).build());
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(SystemRes.builder().status("400").message(e.getMessage()).data(null).build());
        }
    }

    @GetMapping("/get-cursor")
    public ResponseEntity<SystemRes> getBrandCursor(@Validated PanigationRequest request) {
        try {
            Result<PanigationResponse<Brand>, Exception> result = adminBrandUseCase.getBrandsCursor(request);
            if (result.hasError()) {
                return ResponseEntity.badRequest().body(
                        SystemRes.builder().status("400").message(result.error().getMessage()).data(null).build());
            }
            PanigationResponse<Brand> page = result.data();
            PanigationResponse<BrandResponse> responseData = PanigationResponse.<BrandResponse>builder()
                    .cursor(page.getCursor())
                    .limit(page.getLimit())
                    .sort(page.getSort())
                    .hasNext(page.getHasNext())
                    .hasPrevious(page.getHasPrevious())
                    .data(brandMapper.toResponseList(page.getData()))
                    .build();
            return ResponseEntity.ok().body(
                    SystemRes.builder().status("200").message("Get cursor brand success").data(responseData).build());
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(SystemRes.builder().status("400").message(e.getMessage()).data(null).build());
        }
    }
}
