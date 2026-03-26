package io.github.nguyennhatquang.fashion.Catalog.usecase.IUseCase;

import java.time.Instant;
import java.util.List;

import io.github.nguyennhatquang.fashion.Catalog.delivery.Dto.Brand.BrandRequest.BrandCreateRequest;
import io.github.nguyennhatquang.fashion.Catalog.delivery.Dto.Brand.BrandRequest.BrandUpdateRequest;
import io.github.nguyennhatquang.fashion.Catalog.domain.entity.Brand;
import io.github.nguyennhatquang.fashion.common.request.ExactPageRequest;
import io.github.nguyennhatquang.fashion.common.request.PanigationRequest;
import io.github.nguyennhatquang.fashion.common.response.ExactPageResponse;
import io.github.nguyennhatquang.fashion.common.response.PanigationResponse;
import io.github.nguyennhatquang.fashion.common.response.Result;

public interface IAdminBrandUseCase {
    Result<Brand, Exception> createBrand(BrandCreateRequest request);

    Result<Brand, Exception> updateBrand(BrandUpdateRequest request, String id);

    Result<Void, Exception> deleteBrand(String id);

    Result<Brand, Exception> getBrandById(String id);

    Result<ExactPageResponse<Brand>, Exception> GetExactPageResponse(ExactPageRequest request);

    Result<PanigationResponse<Brand>, Exception> getBrandsCursor(PanigationRequest request);

    Result<List<Brand>, Exception> getAllBrand();

}
