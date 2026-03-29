package io.github.nguyennhatquang.fashion.Catalog.usecase.AdapterUseCase;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import io.github.nguyennhatquang.fashion.Catalog.domain.entity.Product;
import io.github.nguyennhatquang.fashion.Catalog.domain.entity.SkuVariant;
import io.github.nguyennhatquang.fashion.Catalog.domain.IRepository.IProductRepository;
import io.github.nguyennhatquang.fashion.Catalog.domain.IRepository.ISkuVariantRepository;
import io.github.nguyennhatquang.fashion.Catalog.usecase.IUseCase.IProductUseCase;
import io.github.nguyennhatquang.fashion.common.infrastructure.Function.MongoCursorPaginationService;
import io.github.nguyennhatquang.fashion.common.request.PaginationRequestv2;
import io.github.nguyennhatquang.fashion.common.request.PanigationRequest;
import io.github.nguyennhatquang.fashion.common.response.PanigationResponse;
import io.github.nguyennhatquang.fashion.common.response.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductUseCase implements IProductUseCase {
    private final MongoCursorPaginationService paginationService;
    private final IProductRepository productRepository;
    private final ISkuVariantRepository skuVariantRepository;
    @Qualifier("virtualThreadExecutor")
    private final ExecutorService virtualExecutor;
    // Chỉ cho phép sort theo 3 trường này
    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
            "createdAt", "basePrice", "name");

    // Chỉ cho phép client gửi filter các trường này
    private static final Set<String> ALLOWED_FILTER_FIELDS = Set.of(
            "brandId", "categoryIds", "status");

    @Override
    public Result<PanigationResponse<Product>, Exception> getProductsCursor(PaginationRequestv2 request) {
        try {
            PanigationResponse<Product> response = paginationService.execute(
                    request,
                    Product.class,
                    "products",
                    ALLOWED_SORT_FIELDS,
                    ALLOWED_FILTER_FIELDS);
            return Result.success(response);
        } catch (Exception e) {
            log.error("Error getting products cursor", e);
            return Result.error(e);
        }
    }

    @Override
    public Result<Product, Exception> getProductById(String id) {
        try {
            Product product = productRepository.findById(id).orElse(null);
            if (product == null) {
                return Result.error(new Exception("Product not found"));
            }
            return Result.success(product);
        } catch (Exception e) {
            log.error("Error getting product by id", e);
            return Result.error(e);
        }
    }

    @Override
    public Result<List<SkuVariant>, Exception> getSkuVariantByProductId(String id) {
        try {
            List<SkuVariant> skuVariant = skuVariantRepository.findByProductId(id);
            if (skuVariant == null) {
                return Result.error(new Exception("Sku variant not found"));
            }
            return Result.success(skuVariant);
        } catch (Exception e) {
            log.error("Error getting sku variant by id", e);
            return Result.error(e);
        }
    }

    @Override
    public Result<Product, Exception> getProductBySlug(String slug) {
        try {
            return Result.success(productRepository.findBySlug(slug).orElse(null));
        } catch (Exception e) {
            log.error("Error getting product by slug", e);
            return Result.error(e);
        }
    }
}
