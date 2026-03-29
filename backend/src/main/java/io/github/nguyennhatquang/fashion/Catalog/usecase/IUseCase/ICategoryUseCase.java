package io.github.nguyennhatquang.fashion.Catalog.usecase.IUseCase;

import io.github.nguyennhatquang.fashion.Catalog.domain.entity.Category;
import io.github.nguyennhatquang.fashion.common.request.PanigationRequest;
import io.github.nguyennhatquang.fashion.common.response.PanigationResponse;
import io.github.nguyennhatquang.fashion.common.response.Result;

public interface ICategoryUseCase {
    Result<PanigationResponse<Category>, Exception> getCategoriesCursor(PanigationRequest request);
}
