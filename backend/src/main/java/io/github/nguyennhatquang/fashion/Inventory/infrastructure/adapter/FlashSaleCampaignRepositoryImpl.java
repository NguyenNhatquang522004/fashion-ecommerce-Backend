package io.github.nguyennhatquang.fashion.Inventory.infrastructure.adapter;

import io.github.nguyennhatquang.fashion.Inventory.domain.IRepository.postgres.IFlashSaleCampaignRepository;
import io.github.nguyennhatquang.fashion.Inventory.domain.entity.FlashSaleCampaign;
import io.github.nguyennhatquang.fashion.Inventory.infrastructure.repository.FlashSaleCampaignRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class FlashSaleCampaignRepositoryImpl implements IFlashSaleCampaignRepository {

    private final FlashSaleCampaignRepository repository;

    @Override
    public FlashSaleCampaign save(FlashSaleCampaign entity) {
        return repository.save(entity);
    }

    @Override
    public List<FlashSaleCampaign> saveAll(List<FlashSaleCampaign> entities) {
        return repository.saveAll(entities);
    }

    @Override
    public FlashSaleCampaign update(FlashSaleCampaign entity) {
        return repository.save(entity);
    }

    @Override
    public List<FlashSaleCampaign> updateAll(List<FlashSaleCampaign> entities) {
        return repository.saveAll(entities);
    }

    @Override
    public void delete(FlashSaleCampaign entity) {
        repository.delete(entity);
    }

    @Override
    public void deleteById(UUID id) {
        repository.deleteById(id);
    }

    @Override
    public void deleteAll(List<FlashSaleCampaign> entities) {
        repository.deleteAll(entities);
    }

    @Override
    public void softDeleteById(UUID id) {
        repository.deleteById(id); // Entity has @SQLDelete, so calling deleteById will act as soft delete
    }

    @Override
    public Optional<FlashSaleCampaign> findById(UUID id) {
        return repository.findById(id);
    }

    @Override
    public List<FlashSaleCampaign> findAll() {
        return repository.findAll();
    }
}
