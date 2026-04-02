package io.github.nguyennhatquang.fashion.Inventory.usecase.AdapterUseCase;

import java.util.List;
import java.util.UUID;
import java.util.Set;

import org.springframework.stereotype.Service;

import io.github.nguyennhatquang.fashion.Inventory.delivery.Dto.FlashSaleItem.FlashSaleItemRequest.FlashSaleItemCreateRequest;
import io.github.nguyennhatquang.fashion.Inventory.delivery.Dto.FlashSaleItem.FlashSaleItemRequest.FlashSaleItemUpdateRequest;
import io.github.nguyennhatquang.fashion.Inventory.delivery.Mapper.FlashSaleItemMapper;
import io.github.nguyennhatquang.fashion.Inventory.domain.IRepository.postgres.IFlashSaleCampaignRepository;
import io.github.nguyennhatquang.fashion.Inventory.domain.IRepository.postgres.IFlashSaleItemRepository;
import io.github.nguyennhatquang.fashion.Inventory.domain.entity.FlashSaleCampaign;
import io.github.nguyennhatquang.fashion.Inventory.domain.entity.FlashSaleItem;
import io.github.nguyennhatquang.fashion.Inventory.usecase.IUseCase.IAdminFlashSaleItemUseCase;
import io.github.nguyennhatquang.fashion.common.infrastructure.Function.JpaExactPagePaginationService;
import io.github.nguyennhatquang.fashion.common.request.ExactPageRequestv2;
import io.github.nguyennhatquang.fashion.common.response.ExactPageResponse;
import io.github.nguyennhatquang.fashion.common.response.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminFlashSaleItemUseCase implements IAdminFlashSaleItemUseCase {
    private final IFlashSaleItemRepository flashSaleItemRepository;
    private final FlashSaleItemMapper flashSaleItemMapper;
    private final IFlashSaleCampaignRepository flashSaleCampaignRepository;

        private final JpaExactPagePaginationService paginationService;

    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
            "createdAt", "updatedAt", "promotionalPrice", "totalQuota");

    private static final Set<String> ALLOWED_FILTER_FIELDS = Set.of(
            "skuCode");

    @Override
    public Result<FlashSaleItem, Exception> createFlashSaleItem(FlashSaleItemCreateRequest request) {
        try {
            FlashSaleCampaign campaign = flashSaleCampaignRepository.findById(request.campaignId()).orElse(null);
            if (campaign == null) {
                return Result.error(new Exception("FlashSaleCampaign not found"));
            }
            FlashSaleItem item = flashSaleItemMapper.toEntity(request);
            item.setCampaign(campaign);
            flashSaleItemRepository.save(item);
            return Result.success(item);
        } catch (Exception e) {
            return Result.error(e);
        }
    }

    @Override
    public Result<FlashSaleItem, Exception> updateFlashSaleItem(FlashSaleItemUpdateRequest request, UUID id) {
        try {
            FlashSaleItem item = flashSaleItemRepository.findById(id).orElse(null);
            if (item == null) {
                return Result.error(new Exception("FlashSaleItem not found"));
            }
            flashSaleItemMapper.updateEntityFromRequest(request, item);
            flashSaleItemRepository.save(item);
            return Result.success(item);
        } catch (Exception e) {
            return Result.error(e);
        }
    }

    @Override
    public Result<Void, Exception> deleteFlashSaleItem(UUID id) {
        try {
            flashSaleItemRepository.deleteById(id);
            return Result.success(null);
        } catch (Exception e) {
            return Result.error(e);
        }
    }

    @Override
    public Result<FlashSaleItem, Exception> getFlashSaleItemById(UUID id) {
        try {
            FlashSaleItem item = flashSaleItemRepository.findById(id).orElse(null);
            if (item == null) {
                return Result.error(new Exception("FlashSaleItem not found"));
            }
            return Result.success(item);
        } catch (Exception e) {
            return Result.error(e);
        }
    }

    @Override
    public Result<ExactPageResponse<FlashSaleItem>, Exception> getAllFlashSaleItems(ExactPageRequestv2 request) {
        try {
            ExactPageResponse<FlashSaleItem> response = paginationService.execute(
                    request,
                    FlashSaleItem.class,
                    ALLOWED_SORT_FIELDS,
                    ALLOWED_FILTER_FIELDS
            );
            return Result.success(response);
        } catch (Exception e) {
            return Result.error(e);
        }
    }
}
