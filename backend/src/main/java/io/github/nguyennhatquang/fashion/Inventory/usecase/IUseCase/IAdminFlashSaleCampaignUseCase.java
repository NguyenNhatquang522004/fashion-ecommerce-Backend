package io.github.nguyennhatquang.fashion.Inventory.usecase.IUseCase;

import java.util.List;
import java.util.UUID;

import io.github.nguyennhatquang.fashion.Inventory.delivery.Dto.FlashSaleCampaign.FlashSaleCampaignRequest.FlashSaleCampaignCreateRequest;
import io.github.nguyennhatquang.fashion.Inventory.delivery.Dto.FlashSaleCampaign.FlashSaleCampaignRequest.FlashSaleCampaignUpdateRequest;
import io.github.nguyennhatquang.fashion.Inventory.domain.entity.FlashSaleCampaign;
import io.github.nguyennhatquang.fashion.common.request.ExactPageRequestv2;
import io.github.nguyennhatquang.fashion.common.response.ExactPageResponse;
import io.github.nguyennhatquang.fashion.common.response.Result;

public interface IAdminFlashSaleCampaignUseCase {
    Result<FlashSaleCampaign, Exception> createCampaign(FlashSaleCampaignCreateRequest request);

    Result<FlashSaleCampaign, Exception> updateCampaign(FlashSaleCampaignUpdateRequest request, UUID id);

    Result<Void, Exception> deleteCampaign(UUID id);

    Result<FlashSaleCampaign, Exception> getCampaignById(UUID id);

    Result<ExactPageResponse<FlashSaleCampaign>, Exception> getAllCampaigns(ExactPageRequestv2 request);
}
