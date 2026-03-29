package io.github.nguyennhatquang.fashion.Catalog.usecase.IUseCase;

import io.github.nguyennhatquang.fashion.Catalog.domain.entity.Brand;
import io.github.nguyennhatquang.fashion.common.request.ExactPageRequest;
import io.github.nguyennhatquang.fashion.common.request.PanigationRequest;
import io.github.nguyennhatquang.fashion.common.response.ExactPageResponse;
import io.github.nguyennhatquang.fashion.common.response.PanigationResponse;
import io.github.nguyennhatquang.fashion.common.response.Result;

public interface IBrandUseCase {
    Result<PanigationResponse<Brand>, Exception> getBrandsCursor(PanigationRequest request);

}
