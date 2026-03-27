package io.github.nguyennhatquang.fashion.Catalog.usecase.IUseCase;

import java.util.List;

import org.springframework.stereotype.Service;

import io.github.nguyennhatquang.fashion.Catalog.delivery.Dto.Product.ProductRequest.ProductCreateRequest;
import io.github.nguyennhatquang.fashion.Catalog.delivery.Dto.Product.ProductRequest.ProductUpdateRequest;
import io.github.nguyennhatquang.fashion.Catalog.delivery.Dto.SkuVariant.SkuVariantRequest.SkuVariantCreateRequest;
import io.github.nguyennhatquang.fashion.Catalog.delivery.Dto.SkuVariant.SkuVariantRequest.SkuVariantUpdateRequest;
import io.github.nguyennhatquang.fashion.Catalog.domain.entity.SkuVariant;
import io.github.nguyennhatquang.fashion.common.request.ExactPageRequest;
import io.github.nguyennhatquang.fashion.common.request.PanigationRequest;
import io.github.nguyennhatquang.fashion.common.response.ExactPageResponse;
import io.github.nguyennhatquang.fashion.common.response.PanigationResponse;
import io.github.nguyennhatquang.fashion.common.response.Result;

public interface IAdminSkuVariantUseCase {
    Result<SkuVariant, Exception> createSkuVariant(SkuVariantCreateRequest request);

    Result<SkuVariant, Exception> updateSkuVariant(String Id, SkuVariantUpdateRequest request);

    Result<SkuVariant, Exception> deleteSkuVariantById(String Id);

    Result<Void, Exception> deleteSkuVariantByProductId(String productId);

    Result<SkuVariant, Exception> getSkuVariantById(String id);

    Result<List<SkuVariant>, Exception> getSkuVariantByProductId(String slug);

    Result<ExactPageResponse<SkuVariant>, Exception> GetExactPageResponse(ExactPageRequest request);

    Result<PanigationResponse<SkuVariant>, Exception> getBrandsCursor(PanigationRequest request);
}
