package io.github.nguyennhatquang.fashion.Catalog.delivery.Handler;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.nguyennhatquang.fashion.Catalog.delivery.Dto.Category.CategoryRequest.CategoryCreateRequest;
import io.github.nguyennhatquang.fashion.Catalog.delivery.Dto.Category.CategoryRequest.CategoryUpdateRequest;
import io.github.nguyennhatquang.fashion.Catalog.delivery.Dto.Category.CategoryResponse;
import io.github.nguyennhatquang.fashion.Catalog.delivery.Mapper.CategoryMapper;
import io.github.nguyennhatquang.fashion.Catalog.domain.entity.Category;
import io.github.nguyennhatquang.fashion.Catalog.usecase.IUseCase.IAdminCategoryUseCase;
import io.github.nguyennhatquang.fashion.common.request.ExactPageRequest;
import io.github.nguyennhatquang.fashion.common.request.PanigationRequest;
import io.github.nguyennhatquang.fashion.common.response.ExactPageResponse;
import io.github.nguyennhatquang.fashion.common.response.PanigationResponse;
import io.github.nguyennhatquang.fashion.common.response.Result;
import io.github.nguyennhatquang.fashion.common.response.SystemRes;
import lombok.RequiredArgsConstructor;

import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/admin/categories")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminCategoryHandler {

    private final IAdminCategoryUseCase adminCategoryUseCase;
    private final CategoryMapper categoryMapper;

    @PostMapping("/create")
    public ResponseEntity<SystemRes> createCategory(@Validated @RequestBody CategoryCreateRequest request) {
        try {
            Result<Category, Exception> result = adminCategoryUseCase.createCategory(request);
            if (result.hasError()) {
                return ResponseEntity.badRequest().body(
                        SystemRes.builder().status("400").message(result.error().getMessage()).data(null).build());
            }
            CategoryResponse response = categoryMapper.toResponse(result.data());
            return ResponseEntity.ok().body(
                    SystemRes.builder().status("200").message("Create category success").data(response).build());
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(SystemRes.builder().status("400").message(e.getMessage()).data(null).build());
        }
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<SystemRes> updateCategory(@PathVariable("id") String id,
            @Validated @RequestBody CategoryUpdateRequest request) {
        try {
            Result<Category, Exception> result = adminCategoryUseCase.updateCategory(id, request);
            if (result.hasError()) {
                return ResponseEntity.badRequest().body(
                        SystemRes.builder().status("400").message(result.error().getMessage()).data(null).build());
            }
            CategoryResponse response = categoryMapper.toResponse(result.data());
            return ResponseEntity.ok().body(
                    SystemRes.builder().status("200").message("Update category success").data(response).build());
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(SystemRes.builder().status("400").message(e.getMessage()).data(null).build());
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<SystemRes> deleteCategory(@PathVariable("id") String id) {
        try {
            Result<Void, Exception> result = adminCategoryUseCase.deleteCategory(id);
            if (result.hasError()) {
                return ResponseEntity.badRequest().body(
                        SystemRes.builder().status("400").message(result.error().getMessage()).data(null).build());
            }
            return ResponseEntity.ok().body(
                    SystemRes.builder().status("200").message("Delete category success").data(null).build());
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(SystemRes.builder().status("400").message(e.getMessage()).data(null).build());
        }
    }

    @GetMapping("/get-detail/{id}")
    public ResponseEntity<SystemRes> getCategoryById(@PathVariable("id") String id) {
        try {
            Result<Category, Exception> result = adminCategoryUseCase.getCategoryById(id);
            if (result.hasError()) {
                return ResponseEntity.badRequest().body(
                        SystemRes.builder().status("400").message(result.error().getMessage()).data(null).build());
            }
            CategoryResponse response = categoryMapper.toResponse(result.data());
            return ResponseEntity.ok().body(
                    SystemRes.builder().status("200").message("Get category success").data(response).build());
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(SystemRes.builder().status("400").message(e.getMessage()).data(null).build());
        }
    }

    @GetMapping("/get-page")
    public ResponseEntity<SystemRes> getPageCategory(@Validated ExactPageRequest request) {
        try {
            Result<ExactPageResponse<Category>, Exception> result = adminCategoryUseCase.GetExactPageResponse(request);
            if (result.hasError()) {
                return ResponseEntity.badRequest().body(
                        SystemRes.builder().status("400").message(result.error().getMessage()).data(null).build());
            }
            ExactPageResponse<Category> page = result.data();
            ExactPageResponse<CategoryResponse> responseData = ExactPageResponse.<CategoryResponse>builder()
                    .currentPage(page.getCurrentPage())
                    .totalPages(page.getTotalPages())
                    .totalElements(page.getTotalElements())
                    .snapshotTime(page.getSnapshotTime())
                    .data(categoryMapper.toResponseList(page.getData()))
                    .build();
            return ResponseEntity.ok().body(
                    SystemRes.builder().status("200").message("Get page category success").data(responseData).build());
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(SystemRes.builder().status("400").message(e.getMessage()).data(null).build());
        }
    }

    @GetMapping("/get-cursor")
    public ResponseEntity<SystemRes> getCategoryCursor(@Validated PanigationRequest request) {
        try {
            Result<PanigationResponse<Category>, Exception> result = adminCategoryUseCase.getCategorysCursor(request);
            if (result.hasError()) {
                return ResponseEntity.badRequest().body(
                        SystemRes.builder().status("400").message(result.error().getMessage()).data(null).build());
            }
            PanigationResponse<Category> page = result.data();
            PanigationResponse<CategoryResponse> responseData = PanigationResponse.<CategoryResponse>builder()
                    .cursor(page.getCursor())
                    .limit(page.getLimit())
                    .sort(page.getSort())
                    .hasNext(page.getHasNext())
                    .hasPrevious(page.getHasPrevious())
                    .data(categoryMapper.toResponseList(page.getData()))
                    .build();
            return ResponseEntity.ok().body(
                    SystemRes.builder().status("200").message("Get cursor category success").data(responseData)
                            .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(SystemRes.builder().status("400").message(e.getMessage()).data(null).build());
        }
    }
}
