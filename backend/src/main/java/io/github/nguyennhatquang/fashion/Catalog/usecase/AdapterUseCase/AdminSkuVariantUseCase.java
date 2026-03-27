package io.github.nguyennhatquang.fashion.Catalog.usecase.AdapterUseCase;

import java.util.List;

import org.springframework.stereotype.Service;

import io.github.nguyennhatquang.fashion.Catalog.delivery.Dto.SkuVariant.SkuVariantRequest.SkuVariantCreateRequest;
import io.github.nguyennhatquang.fashion.Catalog.delivery.Dto.SkuVariant.SkuVariantRequest.SkuVariantUpdateRequest;
import io.github.nguyennhatquang.fashion.Catalog.delivery.Mapper.SkuVariantMapper;
import io.github.nguyennhatquang.fashion.Catalog.domain.IRepository.ISkuVariantRepository;
import io.github.nguyennhatquang.fashion.Catalog.domain.entity.SkuVariant;
import io.github.nguyennhatquang.fashion.Catalog.usecase.IUseCase.IAdminSkuVariantUseCase;
import io.github.nguyennhatquang.fashion.Catalog.utils.SlugHepler;
import io.github.nguyennhatquang.fashion.common.request.ExactPageRequest;
import io.github.nguyennhatquang.fashion.common.request.PanigationRequest;
import io.github.nguyennhatquang.fashion.common.response.ExactPageResponse;
import io.github.nguyennhatquang.fashion.common.response.PanigationResponse;
import io.github.nguyennhatquang.fashion.common.response.Result;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminSkuVariantUseCase implements IAdminSkuVariantUseCase {

    private final ISkuVariantRepository skuVariantRepository;
    private final SkuVariantMapper mapper;

    @Override
    public Result<SkuVariant, Exception> createSkuVariant(SkuVariantCreateRequest request) {
        try {
            SkuVariant skuVariant = mapper.toEntity(request);
            skuVariant.setSkuCode(
                    SlugHepler.generateSkuCode(request.brandName(), request.productId(), skuVariant.getAttributes()));
            skuVariant = skuVariantRepository.save(skuVariant);
            return Result.success(skuVariant);
        } catch (Exception e) {
            return Result.error(e);
        }
    }

    @Override
    public Result<SkuVariant, Exception> updateSkuVariant(String Id, SkuVariantUpdateRequest request) {
        // TODO Auto-generated method stub
        try {
            SkuVariant skuVariant = skuVariantRepository.findById(Id)
                    .orElseThrow(() -> new Exception("SkuVariant not found"));

            mapper.updateEntityFromRequest(request, skuVariant);
            skuVariant = skuVariantRepository.save(skuVariant);
            return Result.success(skuVariant);
        } catch (Exception e) {
            return Result.error(e);
        }
    }

    @Override
    public Result<Void, Exception> deleteSkuVariantByProductId(String productId) {
        try {
            skuVariantRepository.softDeleteByProductId(productId);
            return Result.success(null);
        } catch (Exception e) {
            return Result.error(e);
        }
    }

    @Override
    public Result<SkuVariant, Exception> deleteSkuVariantById(String Id) {
        try {

            skuVariantRepository.softDeleteById(Id);
            return Result.success(null);

        } catch (Exception e) {
            return Result.error(e);
        }
    }

    @Override
    public Result<SkuVariant, Exception> getSkuVariantById(String id) {
        try {
            SkuVariant skuVariant = skuVariantRepository.findById(id)
                    .orElseThrow(() -> new Exception("SkuVariant not found"));
            return Result.success(skuVariant);
        } catch (Exception e) {
            return Result.error(e);
        }
    }

    @Override
    public Result<List<SkuVariant>, Exception> getSkuVariantByProductId(String slug) {
        try {
            List<SkuVariant> skuVariants = skuVariantRepository.findByProductId(slug);
            return Result.success(skuVariants);
        } catch (Exception e) {
            return Result.error(e);
        }
    }

    @Override
    public Result<ExactPageResponse<SkuVariant>, Exception> GetExactPageResponse(ExactPageRequest request) {
        try {
            ExactPageResponse<SkuVariant> exactPageResponse = skuVariantRepository.getSkuVariantsExactPage(request);
            return Result.success(exactPageResponse);
        } catch (Exception e) {
            return Result.error(e);
        }
    }

    @Override
    public Result<PanigationResponse<SkuVariant>, Exception> getBrandsCursor(PanigationRequest request) {
        try {
            PanigationResponse<SkuVariant> panigationResponse = skuVariantRepository.getSkuVariantsCursor(request);
            return Result.success(panigationResponse);
        } catch (Exception e) {
            return Result.error(e);
        }
    }
}
