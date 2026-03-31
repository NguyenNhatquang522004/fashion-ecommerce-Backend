package io.github.nguyennhatquang.fashion.Order.infrastructure.Adapter;

import io.github.nguyennhatquang.fashion.Order.domain.IRepository.postgres.IVoucherRepository;
import io.github.nguyennhatquang.fashion.Order.domain.entity.Voucher;
import io.github.nguyennhatquang.fashion.Order.infrastructure.Repository.VoucherRepository;
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
public class VoucherRepositoryImpl implements IVoucherRepository {

    private final VoucherRepository repository;

    @Override
    public Voucher save(Voucher entity) {
        return repository.save(entity);
    }

    @Override
    public List<Voucher> saveAll(List<Voucher> entities) {
        return repository.saveAll(entities);
    }

    @Override
    public Voucher update(Voucher entity) {
        return repository.save(entity);
    }

    @Override
    public List<Voucher> updateAll(List<Voucher> entities) {
        return repository.saveAll(entities);
    }

    @Override
    public void delete(Voucher entity) {
        repository.delete(entity);
    }

    @Override
    public void deleteById(UUID id) {
        repository.deleteById(id);
    }

    @Override
    public void deleteAll(List<Voucher> entities) {
        repository.deleteAll(entities);
    }

    @Override
    public void softDeleteById(UUID id) {
        repository.deleteById(id);
    }

    @Override
    public Optional<Voucher> findById(UUID id) {
        return repository.findById(id);
    }

    @Override
    public List<Voucher> findAll() {
        return repository.findAll();
    }

    @Override
    public ExactPageResponse<Voucher> getVoucherExactPage(ExactPageRequest request) {
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

        List<Voucher> responseData = Collections.emptyList();

        if (totalElements > 0) {
            List<UUID> ids = repository.findIdsBySnapshot(currentSnapshot, pageable);
            if (!ids.isEmpty()) {
                responseData = repository.fetchFullDataByIds(ids);
            }
        }
        LocalDateTime convertlocaldatetime = ConvertUtils.toLocalDateTime(currentSnapshot);
        return ExactPageResponse.<Voucher>builder()
                .currentPage(request.getPage())
                .totalPages(totalPages)
                .totalElements(totalElements)
                .snapshotTime(convertlocaldatetime)
                .data(responseData)
                .build();
    }
}
