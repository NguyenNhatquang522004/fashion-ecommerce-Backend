package io.github.nguyennhatquang.fashion.Catalog.delivery.Handler;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.nguyennhatquang.fashion.Catalog.delivery.Dto.Brand.BrandResponse;
import io.github.nguyennhatquang.fashion.Catalog.delivery.Mapper.BrandMapper;
import io.github.nguyennhatquang.fashion.Catalog.domain.entity.Brand;
import io.github.nguyennhatquang.fashion.Catalog.usecase.IUseCase.IBrandUseCase;
import io.github.nguyennhatquang.fashion.common.request.PanigationRequest;
import io.github.nguyennhatquang.fashion.common.response.PanigationResponse;
import io.github.nguyennhatquang.fashion.common.response.Result;
import io.github.nguyennhatquang.fashion.common.response.SystemRes;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/brands")
@RequiredArgsConstructor
public class BrandHandler {

    private final IBrandUseCase brandUseCase;
    private final BrandMapper brandMapper;

    @GetMapping("/get-cursor")
    public ResponseEntity<SystemRes> getBrandCursor(@Validated PanigationRequest request) {
        try {
            Result<PanigationResponse<Brand>, Exception> result = brandUseCase.getBrandsCursor(request);
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
