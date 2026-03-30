package io.github.nguyennhatquang.fashion.Inventory.domain.IRepository.postgres;

import io.github.nguyennhatquang.fashion.Inventory.domain.entity.FlashSaleCampaign;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IFlashSaleCampaignRepository {
    FlashSaleCampaign save(FlashSaleCampaign entity);
    List<FlashSaleCampaign> saveAll(List<FlashSaleCampaign> entities);
    FlashSaleCampaign update(FlashSaleCampaign entity);
    List<FlashSaleCampaign> updateAll(List<FlashSaleCampaign> entities);
    void delete(FlashSaleCampaign entity);
    void deleteById(UUID id);
    void deleteAll(List<FlashSaleCampaign> entities);
    void softDeleteById(UUID id);
    Optional<FlashSaleCampaign> findById(UUID id);
    List<FlashSaleCampaign> findAll();
}
