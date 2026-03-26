package io.github.nguyennhatquang.fashion.Catalog.infrastructure.Adapter.mongo;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import io.github.nguyennhatquang.fashion.Catalog.domain.IRepository.ISkuVariantRepository;
import io.github.nguyennhatquang.fashion.Catalog.domain.entity.SkuVariant;
import io.github.nguyennhatquang.fashion.Catalog.infrastructure.Repository.SkuVariantMongoRepository;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class SkuVariantRepositoryImpl implements ISkuVariantRepository {

    private final SkuVariantMongoRepository skuVariantMongoRepository;

    @Override
    public SkuVariant save(SkuVariant skuVariant) {
        return skuVariantMongoRepository.save(skuVariant);
    }

    @Override
    public List<SkuVariant> saveAll(List<SkuVariant> skuVariants) {
        return skuVariantMongoRepository.saveAll(skuVariants);
    }

    @Override
    public SkuVariant update(SkuVariant skuVariant) {
        return skuVariantMongoRepository.save(skuVariant);
    }

    @Override
    public List<SkuVariant> updateAll(List<SkuVariant> skuVariants) {
        return skuVariantMongoRepository.saveAll(skuVariants);
    }

    @Override
    public void delete(SkuVariant skuVariant) {
        skuVariantMongoRepository.delete(skuVariant);
    }

    @Override
    public void deleteById(String id) {
        skuVariantMongoRepository.deleteById(id);
    }

    @Override
    public void deleteAll(List<SkuVariant> skuVariants) {
        skuVariantMongoRepository.deleteAll(skuVariants);
    }

    @Override
    public void softDeleteById(String id) {
        skuVariantMongoRepository.findById(id).ifPresent(skuVariant -> {
            skuVariant.setIsDeleted(true);
            skuVariantMongoRepository.save(skuVariant);
        });
    }

    @Override
    public void softDeleteBySkuCode(String skuCode) {
        skuVariantMongoRepository.findBySkuCode(skuCode).ifPresent(skuVariant -> {
            skuVariant.setIsDeleted(true);
            skuVariantMongoRepository.save(skuVariant);
        });
    }

    @Override
    public void softDeleteByProductId(String productId) {
        skuVariantMongoRepository.findByProductId(productId).forEach(skuVariant -> {
            skuVariant.setIsDeleted(true);
            skuVariantMongoRepository.save(skuVariant);
        });
    }

    @Override
    public Optional<SkuVariant> findById(String id) {
        return skuVariantMongoRepository.findById(id);
    }

    @Override
    public Optional<SkuVariant> findBySkuCode(String skuCode) {
        return skuVariantMongoRepository.findBySkuCode(skuCode);
    }

    @Override
    public List<SkuVariant> findByProductIdAndIsActiveTrue(String productId) {
        return skuVariantMongoRepository.findByProductIdAndIsActiveTrue(productId);
    }

    @Override
    public List<SkuVariant> findByProductIdAndIsDeletedFalse(String productId) {
        return skuVariantMongoRepository.findByProductIdAndIsDeletedFalse(productId);
    }

    @Override
    public List<SkuVariant> findByBarcode(String barcode) {
        return skuVariantMongoRepository.findByBarcode(barcode);
    }

    @Override
    public List<SkuVariant> findByProductId(String productId) {
        return skuVariantMongoRepository.findByProductId(productId);
    }

    @Override
    public List<SkuVariant> findAll() {
        return skuVariantMongoRepository.findAll();
    }
}
