package io.github.nguyennhatquang.fashion.Catalog.usecase.AdapterUseCase;

import org.springframework.stereotype.Service;

import io.github.nguyennhatquang.fashion.Catalog.domain.IRepository.ICategoryRepository;
import io.github.nguyennhatquang.fashion.Catalog.domain.entity.Category;
import io.github.nguyennhatquang.fashion.Catalog.usecase.IUseCase.ICategoryUseCase;
import io.github.nguyennhatquang.fashion.common.request.PanigationRequest;
import io.github.nguyennhatquang.fashion.common.response.PanigationResponse;
import io.github.nguyennhatquang.fashion.common.response.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class CategoryUseCase implements ICategoryUseCase {
    private final ICategoryRepository categoryRepo;

    @Override
    public Result<PanigationResponse<Category>, Exception> getCategoriesCursor(PanigationRequest request) {
        try {
            PanigationResponse<Category> response = categoryRepo.getCategoriesCursor(request);
            return Result.success(response);
        } catch (Exception e) {
            return Result.error(e);
        }
    }
}
