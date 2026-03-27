package io.github.nguyennhatquang.fashion.Catalog.usecase.IUseCase;

import io.github.nguyennhatquang.fashion.Catalog.delivery.Dto.Product.ProductRequest.ProductCreateRequest;
import io.github.nguyennhatquang.fashion.Catalog.delivery.Dto.Product.ProductRequest.ProductUpdateRequest;
import io.github.nguyennhatquang.fashion.Catalog.domain.entity.Product;
import io.github.nguyennhatquang.fashion.common.request.ExactPageRequest;
import io.github.nguyennhatquang.fashion.common.request.PanigationRequest;
import io.github.nguyennhatquang.fashion.common.response.ExactPageResponse;
import io.github.nguyennhatquang.fashion.common.response.PanigationResponse;
import io.github.nguyennhatquang.fashion.common.response.Result;

public interface IAdminProductUseCase {
    Result<Product, Exception> createProduct(ProductCreateRequest request);

    Result<Product, Exception> updateProduct(String Id, ProductUpdateRequest request);

    Result<Product, Exception> deleteProductBySlug(String slug);

    Result<Product, Exception> deleteProductById(String Id);

    Result<Product, Exception> getProductById(String id);

    Result<Product, Exception> getProductBySlug(String slug);

    Result<ExactPageResponse<Product>, Exception> GetExactPageResponse(ExactPageRequest request);

    Result<PanigationResponse<Product>, Exception> getBrandsCursor(PanigationRequest request);
}
