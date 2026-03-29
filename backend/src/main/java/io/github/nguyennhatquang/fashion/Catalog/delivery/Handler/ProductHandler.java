package io.github.nguyennhatquang.fashion.Catalog.delivery.Handler;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.nguyennhatquang.fashion.Catalog.delivery.Dto.Product.ProductResponse;
import io.github.nguyennhatquang.fashion.Catalog.delivery.Dto.SkuVariant.SkuVariantResponse;
import io.github.nguyennhatquang.fashion.Catalog.delivery.Mapper.ProductMapper;
import io.github.nguyennhatquang.fashion.Catalog.delivery.Mapper.SkuVariantMapper;
import io.github.nguyennhatquang.fashion.Catalog.domain.entity.Product;
import io.github.nguyennhatquang.fashion.Catalog.domain.entity.SkuVariant;
import io.github.nguyennhatquang.fashion.Catalog.usecase.IUseCase.IProductUseCase;
import io.github.nguyennhatquang.fashion.common.request.PaginationRequestv2;
import io.github.nguyennhatquang.fashion.common.response.PanigationResponse;
import io.github.nguyennhatquang.fashion.common.response.Result;
import io.github.nguyennhatquang.fashion.common.response.SystemRes;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductHandler {

    private final IProductUseCase productUseCase;
    private final ProductMapper productMapper;
    private final SkuVariantMapper skuVariantMapper;

    @GetMapping("/get-cursor")
    public ResponseEntity<SystemRes> getProductCursor(@Validated PaginationRequestv2 request) {
        try {
            Result<PanigationResponse<Product>, Exception> result = productUseCase.getProductsCursor(request);
            if (result.hasError()) {
                return ResponseEntity.badRequest().body(
                        SystemRes.builder().status("400").message(result.error().getMessage()).data(null).build());
            }
            PanigationResponse<Product> page = result.data();
            PanigationResponse<ProductResponse> responseData = PanigationResponse.<ProductResponse>builder()
                    .cursor(page.getCursor())
                    .limit(page.getLimit())
                    .sort(page.getSort())
                    .hasNext(page.getHasNext())
                    .hasPrevious(page.getHasPrevious())
                    .data(productMapper.toResponseList(page.getData()))
                    .build();
            return ResponseEntity.ok().body(
                    SystemRes.builder().status("200").message("Get cursor product success").data(responseData).build());
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(SystemRes.builder().status("400").message(e.getMessage()).data(null).build());
        }
    }

    @GetMapping("/get-detail/{id}")
    public ResponseEntity<SystemRes> getProductById(@PathVariable("id") String id) {
        try {
            Result<Product, Exception> result = productUseCase.getProductById(id);
            if (result.hasError()) {
                return ResponseEntity.badRequest().body(
                        SystemRes.builder().status("400").message(result.error().getMessage()).data(null).build());
            }
            ProductResponse response = productMapper.toResponse(result.data());
            return ResponseEntity.ok().body(
                    SystemRes.builder().status("200").message("Get product success").data(response).build());
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(SystemRes.builder().status("400").message(e.getMessage()).data(null).build());
        }
    }

    @GetMapping("/get-by-slug/{slug}")
    public ResponseEntity<SystemRes> getProductBySlug(@PathVariable("slug") String slug) {
        try {
            Result<Product, Exception> result = productUseCase.getProductBySlug(slug);
            if (result.hasError()) {
                return ResponseEntity.badRequest().body(
                        SystemRes.builder().status("400").message(result.error().getMessage()).data(null).build());
            }
            ProductResponse response = productMapper.toResponse(result.data());
            return ResponseEntity.ok().body(
                    SystemRes.builder().status("200").message("Get product by slug success").data(response).build());
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(SystemRes.builder().status("400").message(e.getMessage()).data(null).build());
        }
    }

    @GetMapping("/{id}/sku-variants")
    public ResponseEntity<SystemRes> getSkuVariantByProductId(@PathVariable("id") String id) {
        try {
            Result<List<SkuVariant>, Exception> result = productUseCase.getSkuVariantByProductId(id);
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
}
