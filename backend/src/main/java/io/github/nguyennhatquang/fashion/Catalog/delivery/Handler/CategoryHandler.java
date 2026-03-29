package io.github.nguyennhatquang.fashion.Catalog.delivery.Handler;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.nguyennhatquang.fashion.Catalog.delivery.Dto.Category.CategoryResponse;
import io.github.nguyennhatquang.fashion.Catalog.delivery.Mapper.CategoryMapper;
import io.github.nguyennhatquang.fashion.Catalog.domain.entity.Category;
import io.github.nguyennhatquang.fashion.Catalog.usecase.IUseCase.ICategoryUseCase;
import io.github.nguyennhatquang.fashion.common.request.PanigationRequest;
import io.github.nguyennhatquang.fashion.common.response.PanigationResponse;
import io.github.nguyennhatquang.fashion.common.response.Result;
import io.github.nguyennhatquang.fashion.common.response.SystemRes;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryHandler {

    private final ICategoryUseCase categoryUseCase;
    private final CategoryMapper categoryMapper;

    @GetMapping("/get-cursor")
    public ResponseEntity<SystemRes> getCategoryCursor(@Validated PanigationRequest request) {
        try {
            Result<PanigationResponse<Category>, Exception> result = categoryUseCase.getCategoriesCursor(request);
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
