package io.github.nguyennhatquang.fashion.Inventory.usecase.AdapterUseCase;

import java.util.List;
import java.util.UUID;
import java.util.Set;

import org.springframework.stereotype.Service;

import io.github.nguyennhatquang.fashion.Inventory.delivery.Dto.FlashSaleCampaign.FlashSaleCampaignRequest.FlashSaleCampaignCreateRequest;
import io.github.nguyennhatquang.fashion.Inventory.delivery.Dto.FlashSaleCampaign.FlashSaleCampaignRequest.FlashSaleCampaignUpdateRequest;
import io.github.nguyennhatquang.fashion.Inventory.delivery.Dto.FlashSaleItem.FlashSaleItemRequest.FlashSaleItemCreateRequest;
import io.github.nguyennhatquang.fashion.Inventory.delivery.Mapper.FlashSaleCampaignMapper;
import io.github.nguyennhatquang.fashion.Inventory.delivery.Mapper.FlashSaleItemMapper;
import io.github.nguyennhatquang.fashion.Inventory.domain.IRepository.postgres.IFlashSaleCampaignRepository;
import io.github.nguyennhatquang.fashion.Inventory.domain.IRepository.postgres.IFlashSaleItemRepository;
import io.github.nguyennhatquang.fashion.Inventory.domain.entity.FlashSaleCampaign;
import io.github.nguyennhatquang.fashion.Inventory.domain.entity.FlashSaleItem;
import io.github.nguyennhatquang.fashion.Inventory.usecase.IUseCase.IAdminFlashSaleCampaignUseCase;
import io.github.nguyennhatquang.fashion.common.infrastructure.Function.JpaExactPagePaginationService;
import io.github.nguyennhatquang.fashion.common.request.ExactPageRequestv2;
import io.github.nguyennhatquang.fashion.common.response.ExactPageResponse;
import io.github.nguyennhatquang.fashion.common.response.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminFlashSaleCampaignUseCase implements IAdminFlashSaleCampaignUseCase {
    private final IFlashSaleCampaignRepository campaignRepository;
    private final FlashSaleCampaignMapper campaignMapper;
    private final IFlashSaleItemRepository flashSaleItemRepository;
    private final FlashSaleItemMapper flashSaleItemMapper;
    private final JpaExactPagePaginationService paginationService;

    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
            "createdAt", "updatedAt", "startTime", "endTime");

    private static final Set<String> ALLOWED_FILTER_FIELDS = Set.of(
            "status", "name");

    @Override
    public Result<FlashSaleCampaign, Exception> createCampaign(FlashSaleCampaignCreateRequest request,
            List<FlashSaleItemCreateRequest> requestFlashSaleItem) {
        try {
            FlashSaleCampaign campaign = campaignMapper.toEntity(request);
            campaignRepository.save(campaign);
            for (FlashSaleItemCreateRequest itemRequest : requestFlashSaleItem) {
                FlashSaleItem item = flashSaleItemMapper.toEntity(itemRequest);
                item.setCampaign(campaign);
                campaign.getItems().add(item);
            }
            campaignRepository.save(campaign);
            return Result.success(campaign);
        } catch (Exception e) {
            return Result.error(e);
        }
    }

    @Override
    public Result<FlashSaleCampaign, Exception> updateCampaign(FlashSaleCampaignUpdateRequest request, UUID id) {
        try {
            FlashSaleCampaign campaign = campaignRepository.findById(id).orElse(null);
            if (campaign == null) {
                return Result.error(new Exception("FlashSaleCampaign not found"));
            }
            campaignMapper.updateEntityFromRequest(request, campaign);
            campaignRepository.save(campaign);
            return Result.success(campaign);
        } catch (Exception e) {
            return Result.error(e);
        }
    }

    @Override
    public Result<Void, Exception> deleteCampaign(UUID id) {
        try {
            campaignRepository.deleteById(id);
            return Result.success(null);
        } catch (Exception e) {
            return Result.error(e);
        }
    }

    @Override
    public Result<FlashSaleCampaign, Exception> getCampaignById(UUID id) {
        try {
            FlashSaleCampaign campaign = campaignRepository.findById(id).orElse(null);
            if (campaign == null) {
                return Result.error(new Exception("FlashSaleCampaign not found"));
            }
            return Result.success(campaign);
        } catch (Exception e) {
            return Result.error(e);
        }
    }

    @Override
    public Result<ExactPageResponse<FlashSaleCampaign>, Exception> getAllCampaigns(ExactPageRequestv2 request) {
        try {
            ExactPageResponse<FlashSaleCampaign> response = paginationService.execute(
                    request,
                    FlashSaleCampaign.class,
                    ALLOWED_SORT_FIELDS,
                    ALLOWED_FILTER_FIELDS);
            return Result.success(response);
        } catch (Exception e) {
            return Result.error(e);
        }
    }
}
