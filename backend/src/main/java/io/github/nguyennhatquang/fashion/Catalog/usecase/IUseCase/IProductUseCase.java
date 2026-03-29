package io.github.nguyennhatquang.fashion.Catalog.usecase.IUseCase;

import java.util.List;

import io.github.nguyennhatquang.fashion.Catalog.domain.entity.Brand;
import io.github.nguyennhatquang.fashion.Catalog.domain.entity.Product;
import io.github.nguyennhatquang.fashion.Catalog.domain.entity.SkuVariant;
import io.github.nguyennhatquang.fashion.common.request.PaginationRequestv2;
import io.github.nguyennhatquang.fashion.common.request.PanigationRequest;
import io.github.nguyennhatquang.fashion.common.response.PanigationResponse;
import io.github.nguyennhatquang.fashion.common.response.Result;

public interface IProductUseCase {
    Result<PanigationResponse<Product>, Exception> getProductsCursor(PaginationRequestv2 request);

    Result<Product, Exception> getProductById(String id);

    Result<List<SkuVariant>, Exception> getSkuVariantByProductId(String id);

    Result<Product, Exception> getProductBySlug(String slug);
}
