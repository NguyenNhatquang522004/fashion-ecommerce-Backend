package io.github.nguyennhatquang.fashion.Catalog.usecase.IUseCase;

import io.github.nguyennhatquang.fashion.Catalog.delivery.Dto.Category.CategoryRequest;
import io.github.nguyennhatquang.fashion.Catalog.delivery.Dto.Category.CategoryResponse;
import io.github.nguyennhatquang.fashion.Catalog.domain.entity.Category;
import io.github.nguyennhatquang.fashion.common.request.ExactPageRequest;
import io.github.nguyennhatquang.fashion.common.request.PanigationRequest;
import io.github.nguyennhatquang.fashion.common.response.ExactPageResponse;
import io.github.nguyennhatquang.fashion.common.response.PanigationResponse;
import io.github.nguyennhatquang.fashion.common.response.Result;

public interface IAdminCategoryUseCase {
    // 1. Create
    Result<Category, Exception> createCategory(CategoryRequest.CategoryCreateRequest request);

    // 2. Update
    Result<Category, Exception> updateCategory(String id, CategoryRequest.CategoryUpdateRequest request);

    // 3. Delete (Soft Delete)
    Result<Void, Exception> deleteCategory(String id);

    // 4. Get By Id
    Result<Category, Exception> getCategoryById(String id);

    // 5. Get All (Có phân trang)

    Result<ExactPageResponse<Category>, Exception> GetExactPageResponse(ExactPageRequest request);

    Result<PanigationResponse<Category>, Exception> getCategorysCursor(PanigationRequest request);
}
