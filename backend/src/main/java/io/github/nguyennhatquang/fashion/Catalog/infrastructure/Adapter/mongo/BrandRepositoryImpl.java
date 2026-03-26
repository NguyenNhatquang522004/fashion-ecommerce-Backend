package io.github.nguyennhatquang.fashion.Catalog.infrastructure.Adapter.mongo;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import io.github.nguyennhatquang.fashion.Catalog.domain.IRepository.IBrandRepository;
import io.github.nguyennhatquang.fashion.Catalog.domain.entity.Brand;
import io.github.nguyennhatquang.fashion.Catalog.infrastructure.Repository.BrandMongoRepository;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class BrandRepositoryImpl implements IBrandRepository {

    private final BrandMongoRepository brandMongoRepository;

    @Override
    public Brand save(Brand brand) {
        return brandMongoRepository.save(brand);
    }

    @Override
    public List<Brand> saveAll(List<Brand> brands) {
        return brandMongoRepository.saveAll(brands);
    }

    @Override
    public Brand update(Brand brand) {
        return brandMongoRepository.save(brand);
    }

    @Override
    public List<Brand> updateAll(List<Brand> brands) {
        return brandMongoRepository.saveAll(brands);
    }

    @Override
    public void delete(Brand brand) {
        brandMongoRepository.delete(brand);
    }

    @Override
    public void deleteById(String id) {
        brandMongoRepository.deleteById(id);
    }

    @Override
    public void deleteAll(List<Brand> brands) {
        brandMongoRepository.deleteAll(brands);
    }

    @Override
    public void softDeleteById(String id) {
        brandMongoRepository.findById(id).ifPresent(brand -> {
            brand.setIsDeleted(true);
            brandMongoRepository.save(brand);
        });
    }

    @Override
    public void softDeleteBySlug(String slug) {
        brandMongoRepository.findBySlug(slug).ifPresent(brand -> {
            brand.setIsDeleted(true);
            brandMongoRepository.save(brand);
        });
    }

    @Override
    public Optional<Brand> findById(String id) {
        return brandMongoRepository.findById(id);
    }

    @Override
    public Optional<Brand> findBySlug(String slug) {
        return brandMongoRepository.findBySlug(slug);
    }

    @Override
    public List<Brand> findAll() {
        return brandMongoRepository.findAll();
    }
}
