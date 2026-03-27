package io.github.nguyennhatquang.fashion.Catalog.usecase.AdapterUseCase;

import java.util.List;

import org.springframework.stereotype.Service;

import io.github.nguyennhatquang.fashion.Catalog.delivery.Dto.Brand.BrandRequest.BrandCreateRequest;
import io.github.nguyennhatquang.fashion.Catalog.delivery.Dto.Brand.BrandRequest.BrandUpdateRequest;
import io.github.nguyennhatquang.fashion.Catalog.delivery.Mapper.BrandMapper;
import io.github.nguyennhatquang.fashion.Catalog.domain.IRepository.IBrandRepository;
import io.github.nguyennhatquang.fashion.Catalog.domain.entity.Brand;
import io.github.nguyennhatquang.fashion.Catalog.usecase.IUseCase.IAdminBrandUseCase;
import io.github.nguyennhatquang.fashion.Catalog.utils.SlugHepler;
import io.github.nguyennhatquang.fashion.common.request.ExactPageRequest;
import io.github.nguyennhatquang.fashion.common.request.PanigationRequest;
import io.github.nguyennhatquang.fashion.common.response.ExactPageResponse;
import io.github.nguyennhatquang.fashion.common.response.PanigationResponse;
import io.github.nguyennhatquang.fashion.common.response.Result;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminBrandUseCase implements IAdminBrandUseCase {
    private final IBrandRepository brandRepository;
    private final BrandMapper brandMapper;

    @Override
    public Result<Brand, Exception> createBrand(BrandCreateRequest request) {
        try {
            Brand brand = brandMapper.toEntity(request);
            brand.setSlug(SlugHepler.generateUniqueSlug(brand.getName()));
            brandRepository.save(brand);
            return Result.success(brand);
        } catch (Exception e) {
            return Result.error(e);
        }
    }

    @Override
    public Result<Brand, Exception> updateBrand(BrandUpdateRequest request, String id) {
        try {
            Brand brand = brandRepository.findById(id).orElse(null);
            if (brand == null) {
                return Result.error(new Exception("Brand not found"));
            }
            brandMapper.updateEntityFromRequest(request, brand);
            brand.setSlug(SlugHepler.generateUniqueSlug(brand.getName()));
            brandRepository.save(brand);
            return Result.success(brand);
        } catch (Exception e) {
            return Result.error(e);
        }
    }

    @Override
    public Result<Void, Exception> deleteBrand(String id) {
        try {
            brandRepository.softDeleteById(id);
            return Result.success(null);
        } catch (Exception e) {
            return Result.error(e);
        }
    }

    @Override
    public Result<Brand, Exception> getBrandById(String id) {
        try {
            Brand brand = brandRepository.findById(id).orElse(null);
            if (brand == null) {
                return Result.error(new Exception("Brand not found"));
            }
            return Result.success(brand);
        } catch (Exception e) {
            return Result.error(e);
        }
    }

    @Override
    public Result<ExactPageResponse<Brand>, Exception> GetExactPageResponse(ExactPageRequest request) {
        try {
            ExactPageResponse<Brand> data = brandRepository.getBrandsExactPage(request);
            if (data == null) {
                return null;
            }
            return Result.success(data);
        } catch (Exception e) {
            return Result.error(e);
        }
    }

    @Override
    public Result<PanigationResponse<Brand>, Exception> getBrandsCursor(PanigationRequest request) {
        try {
            PanigationResponse<Brand> data = brandRepository.getBrandsCursor(request);
            if (data == null) {
                return null;
            }
            return Result.success(data);
        } catch (Exception e) {
            return Result.error(e);
        }
    }

    @Override
    public Result<List<Brand>, Exception> getAllBrand() {
        try {
            List<Brand> data = brandRepository.findAll();
            if (data == null) {
                return null;
            }
            return Result.success(data);
        } catch (Exception e) {
            return Result.error(e);
        }
    }
}
