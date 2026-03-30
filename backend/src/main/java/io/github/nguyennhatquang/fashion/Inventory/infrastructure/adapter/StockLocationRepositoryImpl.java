package io.github.nguyennhatquang.fashion.Inventory.infrastructure.adapter;

import io.github.nguyennhatquang.fashion.Inventory.domain.IRepository.postgres.IStockLocationRepository;
import io.github.nguyennhatquang.fashion.Inventory.domain.entity.OutboxEventInventory;
import io.github.nguyennhatquang.fashion.Inventory.domain.entity.StockLocation;
import io.github.nguyennhatquang.fashion.Inventory.infrastructure.repository.StockLocationRepository;
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
public class StockLocationRepositoryImpl implements IStockLocationRepository {

    private final StockLocationRepository repository;

    @Override
    public StockLocation save(StockLocation entity) {
        return repository.save(entity);
    }

    @Override
    public List<StockLocation> saveAll(List<StockLocation> entities) {
        return repository.saveAll(entities);
    }

    @Override
    public StockLocation update(StockLocation entity) {
        return repository.save(entity);
    }

    @Override
    public List<StockLocation> updateAll(List<StockLocation> entities) {
        return repository.saveAll(entities);
    }

    @Override
    public void delete(StockLocation entity) {
        repository.delete(entity);
    }

    @Override
    public void deleteById(UUID id) {
        repository.deleteById(id);
    }

    @Override
    public void deleteAll(List<StockLocation> entities) {
        repository.deleteAll(entities);
    }

    @Override
    public void softDeleteById(UUID id) {
        repository.deleteById(id); // Entity has @SQLDelete, so calling deleteById will act as soft delete
    }

    @Override
    public Optional<StockLocation> findById(UUID id) {
        return repository.findById(id);
    }

    @Override
    public List<StockLocation> findAll() {
        return repository.findAll();
    }

    @Override
    public ExactPageResponse<StockLocation> getStockLocationExactPage(ExactPageRequest request) {
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

        List<StockLocation> responseData = Collections.emptyList();

        if (totalElements > 0) {
            List<UUID> ids = repository.findIdsBySnapshot(currentSnapshot, pageable);
            if (!ids.isEmpty()) {
                responseData = repository.fetchFullDataByIds(ids);
            }
        }
        LocalDateTime convertlocaldatetime = ConvertUtils.toLocalDateTime(currentSnapshot);
        // 4. Trả về Response
        return ExactPageResponse.<StockLocation>builder()
                .currentPage(request.getPage())
                .totalPages(totalPages)
                .totalElements(totalElements)
                .snapshotTime(convertlocaldatetime)
                .data(responseData)
                .build();
    }
}
