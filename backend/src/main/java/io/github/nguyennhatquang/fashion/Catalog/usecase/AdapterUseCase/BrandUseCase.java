package io.github.nguyennhatquang.fashion.Catalog.usecase.AdapterUseCase;

import org.springframework.stereotype.Service;

import io.github.nguyennhatquang.fashion.Catalog.domain.IRepository.IBrandRepository;
import io.github.nguyennhatquang.fashion.Catalog.domain.entity.Brand;
import io.github.nguyennhatquang.fashion.Catalog.usecase.IUseCase.IBrandUseCase;
import io.github.nguyennhatquang.fashion.common.request.PanigationRequest;
import io.github.nguyennhatquang.fashion.common.response.PanigationResponse;
import io.github.nguyennhatquang.fashion.common.response.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class BrandUseCase implements IBrandUseCase {
    private final IBrandRepository brandRepo;

    @Override
    public Result<PanigationResponse<Brand>, Exception> getBrandsCursor(PanigationRequest request) {
        try {
            PanigationResponse<Brand> response = brandRepo.getBrandsCursor(request);
            return Result.success(response);
        } catch (Exception e) {
            return Result.error(e);
        }
    }
}
