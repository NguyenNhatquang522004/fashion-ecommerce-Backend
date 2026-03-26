package io.github.nguyennhatquang.fashion.Catalog.usecase.IUseCase;

import java.time.Instant;
import java.util.List;

import io.github.nguyennhatquang.fashion.Catalog.domain.entity.Brand;
import io.github.nguyennhatquang.fashion.common.request.ExactPageRequest;
import io.github.nguyennhatquang.fashion.common.response.ExactPageResponse;
import io.github.nguyennhatquang.fashion.common.response.Result;

public interface IAdminBrandUseCase {
    Result<Brand, Exception> createBrand(Brand brand);

    Result<Brand, Exception> updateBrand(Brand brand);

    Result<Brand, Exception> deleteBrand(Brand brand);

    Result<Brand, Exception> getBrandById(String id);

    ExactPageResponse<Brand> GetExactPageResponse(ExactPageRequest request);

    Result<List<Brand>, Exception> getAllBrand();

}
