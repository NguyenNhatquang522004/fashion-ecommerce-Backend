package io.github.nguyennhatquang.fashion.Inventory.delivery.Mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import io.github.nguyennhatquang.fashion.Inventory.delivery.Dto.FlashSaleCampaign.FlashSaleCampaignRequest.FlashSaleCampaignCreateRequest;
import io.github.nguyennhatquang.fashion.Inventory.delivery.Dto.FlashSaleCampaign.FlashSaleCampaignRequest.FlashSaleCampaignUpdateRequest;
import io.github.nguyennhatquang.fashion.Inventory.delivery.Dto.FlashSaleCampaign.FlashSaleCampaignResponse;
import io.github.nguyennhatquang.fashion.Inventory.domain.entity.FlashSaleCampaign;

import java.util.List;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface FlashSaleCampaignMapper {

    // 1. Map từ CreateRequest sang Entity
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)     // Mặc định DRAFT khi tạo mới
    @Mapping(target = "items", ignore = true)       // Collection được quản lý riêng
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    FlashSaleCampaign toEntity(FlashSaleCampaignCreateRequest request);

    // 2. Map từ UpdateRequest để cập nhật Entity có sẵn
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)     // Trạng thái đổi qua API riêng
    @Mapping(target = "items", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    void updateEntityFromRequest(FlashSaleCampaignUpdateRequest request, @MappingTarget FlashSaleCampaign campaign);

    // 3. Map từ Entity ra Response
    FlashSaleCampaignResponse toResponse(FlashSaleCampaign campaign);

    List<FlashSaleCampaignResponse> toResponseList(List<FlashSaleCampaign> campaigns);
}
