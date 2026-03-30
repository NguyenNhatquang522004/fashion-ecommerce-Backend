package io.github.nguyennhatquang.fashion.Inventory.infrastructure.adapter;

import io.github.nguyennhatquang.fashion.Inventory.domain.IRepository.postgres.IInventoryLedgerRepository;
import io.github.nguyennhatquang.fashion.Inventory.domain.entity.FlashSaleCampaign;
import io.github.nguyennhatquang.fashion.Inventory.domain.entity.InventoryLedger;
import io.github.nguyennhatquang.fashion.Inventory.infrastructure.repository.InventoryLedgerRepository;
import io.github.nguyennhatquang.fashion.common.Utils.ConvertUtils;
import io.github.nguyennhatquang.fashion.common.request.ExactPageRequest;
import io.github.nguyennhatquang.fashion.common.response.ExactPageResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class InventoryLedgerRepositoryImpl implements IInventoryLedgerRepository {

    private final InventoryLedgerRepository repository;

    @Override
    public InventoryLedger save(InventoryLedger entity) {
        return repository.save(entity);
    }

    @Override
    public List<InventoryLedger> saveAll(List<InventoryLedger> entities) {
        return repository.saveAll(entities);
    }

    @Override
    public InventoryLedger update(InventoryLedger entity) {
        return repository.save(entity);
    }

    @Override
    public List<InventoryLedger> updateAll(List<InventoryLedger> entities) {
        return repository.saveAll(entities);
    }

    @Override
    public void delete(InventoryLedger entity) {
        repository.delete(entity);
    }

    @Override
    public void deleteById(UUID id) {
        repository.deleteById(id);
    }

    @Override
    public void deleteAll(List<InventoryLedger> entities) {
        repository.deleteAll(entities);
    }

    @Override
    public void softDeleteById(UUID id) {
        repository.deleteById(id); // Entity has @SQLDelete, so calling deleteById will act as soft delete
    }

    @Override
    public Optional<InventoryLedger> findById(UUID id) {
        return repository.findById(id);
    }

    @Override
    public List<InventoryLedger> findAll() {
        return repository.findAll();
    }

    @Override
    public ExactPageResponse<InventoryLedger> getInventoryLedgerExactPage(ExactPageRequest request) {
        // 1. Chốt Snapshot Time (Đóng băng Timeline)
        Instant currentSnapshot = request.getSnapshotTime() != null
                ? ConvertUtils.toInstant(request.getSnapshotTime())
                : Instant.now();

        // 2. Cấu hình Pageable (Kèm Sort cực kỳ quan trọng để ăn vào Index)
        int page = Math.max(request.getPage() - 1, 0);
        int limit = request.getLimit() > 0 ? request.getLimit() : 10;

        Pageable pageable = PageRequest.of(page, limit, Sort.by(
                Sort.Order.desc("createdAt"),
                Sort.Order.desc("id")));

        // 3. Lấy tổng số Element để tính Total Pages
        long totalElements = repository.countBySnapshot(currentSnapshot);
        int totalPages = (int) Math.ceil((double) totalElements / limit);

        List<InventoryLedger> responseData = Collections.emptyList();

        if (totalElements > 0) {
            List<UUID> ids = repository.findIdsBySnapshot(currentSnapshot, pageable);
            if (!ids.isEmpty()) {
                responseData = repository.fetchFullDataByIds(ids);
            }
        }
        LocalDateTime convertlocaldatetime = ConvertUtils.toLocalDateTime(currentSnapshot);
        // 4. Trả về Response
        return ExactPageResponse.<InventoryLedger>builder()
                .currentPage(request.getPage())
                .totalPages(totalPages)
                .totalElements(totalElements)
                .snapshotTime(convertlocaldatetime)
                .data(responseData)
                .build();
    }
}
