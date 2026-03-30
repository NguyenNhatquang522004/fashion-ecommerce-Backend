package io.github.nguyennhatquang.fashion.Inventory.usecase.AdapterUseCase;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import io.github.nguyennhatquang.fashion.Inventory.delivery.Dto.FlashSaleCampaign.FlashSaleCampaignRequest.FlashSaleCampaignCreateRequest;
import io.github.nguyennhatquang.fashion.Inventory.delivery.Dto.FlashSaleCampaign.FlashSaleCampaignRequest.FlashSaleCampaignUpdateRequest;
import io.github.nguyennhatquang.fashion.Inventory.delivery.Mapper.FlashSaleCampaignMapper;
import io.github.nguyennhatquang.fashion.Inventory.domain.IRepository.postgres.IFlashSaleCampaignRepository;
import io.github.nguyennhatquang.fashion.Inventory.domain.entity.FlashSaleCampaign;
import io.github.nguyennhatquang.fashion.Inventory.usecase.IUseCase.IAdminFlashSaleCampaignUseCase;
import io.github.nguyennhatquang.fashion.common.request.ExactPageRequest;
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

    @Override
    public Result<FlashSaleCampaign, Exception> createCampaign(FlashSaleCampaignCreateRequest request) {
        try {
            FlashSaleCampaign campaign = campaignMapper.toEntity(request);
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
    public Result<ExactPageResponse<FlashSaleCampaign>, Exception> getAllCampaigns(ExactPageRequest request) {
        try {
            ExactPageResponse<FlashSaleCampaign> campaigns = campaignRepository.getFlashSaleCampaignExactPage(request);
            return Result.success(campaigns);
        } catch (Exception e) {
            return Result.error(e);
        }
    }
}
