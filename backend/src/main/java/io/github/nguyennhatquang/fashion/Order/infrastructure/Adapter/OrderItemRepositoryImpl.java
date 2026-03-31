package io.github.nguyennhatquang.fashion.Order.infrastructure.Adapter;

import io.github.nguyennhatquang.fashion.Order.domain.IRepository.postgres.IOrderItemRepository;
import io.github.nguyennhatquang.fashion.Order.domain.entity.OrderItem;
import io.github.nguyennhatquang.fashion.Order.infrastructure.Repository.OrderItemRepository;
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
public class OrderItemRepositoryImpl implements IOrderItemRepository {

    private final OrderItemRepository repository;

    @Override
    public OrderItem save(OrderItem entity) {
        return repository.save(entity);
    }

    @Override
    public List<OrderItem> saveAll(List<OrderItem> entities) {
        return repository.saveAll(entities);
    }

    @Override
    public OrderItem update(OrderItem entity) {
        return repository.save(entity);
    }

    @Override
    public List<OrderItem> updateAll(List<OrderItem> entities) {
        return repository.saveAll(entities);
    }

    @Override
    public void delete(OrderItem entity) {
        repository.delete(entity);
    }

    @Override
    public void deleteById(UUID id) {
        repository.deleteById(id);
    }

    @Override
    public void deleteAll(List<OrderItem> entities) {
        repository.deleteAll(entities);
    }

    @Override
    public void softDeleteById(UUID id) {
        repository.deleteById(id);
    }

    @Override
    public Optional<OrderItem> findById(UUID id) {
        return repository.findById(id);
    }

    @Override
    public List<OrderItem> findAll() {
        return repository.findAll();
    }

    @Override
    public ExactPageResponse<OrderItem> getOrderItemExactPage(ExactPageRequest request) {
        Instant currentSnapshot = request.getSnapshotTime() != null
                ? ConvertUtils.toInstant(request.getSnapshotTime())
                : Instant.now();

        int page = Math.max(request.getPage() - 1, 0);
        int limit = request.getLimit() > 0 ? request.getLimit() : 10;

        Pageable pageable = PageRequest.of(page, limit, Sort.by(
                Sort.Order.desc("createdAt"),
                Sort.Order.desc("id")));

        long totalElements = repository.countBySnapshot(currentSnapshot);
        int totalPages = (int) Math.ceil((double) totalElements / limit);

        List<OrderItem> responseData = Collections.emptyList();

        if (totalElements > 0) {
            List<UUID> ids = repository.findIdsBySnapshot(currentSnapshot, pageable);
            if (!ids.isEmpty()) {
                responseData = repository.fetchFullDataByIds(ids);
            }
        }
        LocalDateTime convertlocaldatetime = ConvertUtils.toLocalDateTime(currentSnapshot);
        return ExactPageResponse.<OrderItem>builder()
                .currentPage(request.getPage())
                .totalPages(totalPages)
                .totalElements(totalElements)
                .snapshotTime(convertlocaldatetime)
                .data(responseData)
                .build();
    }
}
