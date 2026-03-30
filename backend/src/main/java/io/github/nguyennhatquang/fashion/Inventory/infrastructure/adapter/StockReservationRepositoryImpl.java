package io.github.nguyennhatquang.fashion.Inventory.infrastructure.adapter;

import io.github.nguyennhatquang.fashion.Inventory.domain.IRepository.postgres.IStockReservationRepository;
import io.github.nguyennhatquang.fashion.Inventory.domain.entity.StockReservation;
import io.github.nguyennhatquang.fashion.Inventory.infrastructure.repository.StockReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class StockReservationRepositoryImpl implements IStockReservationRepository {

    private final StockReservationRepository repository;

    @Override
    public StockReservation save(StockReservation entity) {
        return repository.save(entity);
    }

    @Override
    public List<StockReservation> saveAll(List<StockReservation> entities) {
        return repository.saveAll(entities);
    }

    @Override
    public StockReservation update(StockReservation entity) {
        return repository.save(entity);
    }

    @Override
    public List<StockReservation> updateAll(List<StockReservation> entities) {
        return repository.saveAll(entities);
    }

    @Override
    public void delete(StockReservation entity) {
        repository.delete(entity);
    }

    @Override
    public void deleteById(UUID id) {
        repository.deleteById(id);
    }

    @Override
    public void deleteAll(List<StockReservation> entities) {
        repository.deleteAll(entities);
    }

    @Override
    public void softDeleteById(UUID id) {
        repository.deleteById(id); // Entity has @SQLDelete, so calling deleteById will act as soft delete
    }

    @Override
    public Optional<StockReservation> findById(UUID id) {
        return repository.findById(id);
    }

    @Override
    public List<StockReservation> findAll() {
        return repository.findAll();
    }
}
