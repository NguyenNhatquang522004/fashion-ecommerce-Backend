package io.github.nguyennhatquang.fashion.Catalog.delivery.Handler;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.nguyennhatquang.fashion.Catalog.delivery.Dto.Brand.BrandRequest.BrandCreateRequest;
import io.github.nguyennhatquang.fashion.Catalog.delivery.Dto.Brand.BrandRequest.BrandUpdateRequest;
import io.github.nguyennhatquang.fashion.Catalog.domain.entity.Brand;
import io.github.nguyennhatquang.fashion.Catalog.usecase.IUseCase.IAdminBrandUseCase;
import io.github.nguyennhatquang.fashion.common.request.ExactPageRequest;
import io.github.nguyennhatquang.fashion.common.response.ExactPageResponse;
import io.github.nguyennhatquang.fashion.common.response.Result;
import io.github.nguyennhatquang.fashion.common.response.SystemRes;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/admin/brands")
@RequiredArgsConstructor
public class AdminBrandHandler {
    private final IAdminBrandUseCase adminBrandUseCase;

    @PostMapping("/create")
    public ResponseEntity<SystemRes> createBrand(@Validated @RequestBody BrandCreateRequest request) {
        try {
            Result<Brand, Exception> result = adminBrandUseCase.createBrand(request);
            if (result.hasError()) {
                return ResponseEntity.badRequest().body(
                        SystemRes.builder().status("400").message(result.error().getMessage()).data(null).build());
            }
            return ResponseEntity.ok().body(
                    SystemRes.builder().status("200").message("Create brand success").data(result.data()).build());
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
            return ResponseEntity.ok().body(
                    SystemRes.builder().status("200").message("Update brand success").data(result.data()).build());
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(SystemRes.builder().status("400").message(e.getMessage()).data(null).build());
        }

    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<SystemRes> deleteBrand(@PathVariable("id") String id) {
        try {
            Result<Void, Exception> result = adminBrandUseCase.deleteBrand(id);
            if (result.hasError()) {
                return ResponseEntity.badRequest().body(
                        SystemRes.builder().status("400").message(result.error().getMessage()).data(null).build());
            }
            return ResponseEntity.ok().body(
                    SystemRes.builder().status("200").message("Delete brand success").data(result.data()).build());
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
            return ResponseEntity.ok().body(
                    SystemRes.builder().status("200").message("Get brand success").data(result.data()).build());
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
            return ResponseEntity.ok().body(
                    SystemRes.builder().status("200").message("Get all brand success").data(result.data()).build());
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
            return ResponseEntity.ok().body(
                    SystemRes.builder().status("200").message("Get page brand success").data(result.data()).build());
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(SystemRes.builder().status("400").message(e.getMessage()).data(null).build());
        }
    }
}
