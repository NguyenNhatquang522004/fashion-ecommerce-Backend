package io.github.nguyennhatquang.fashion.Catalog.infrastructure.Adapter.mongo;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import io.github.nguyennhatquang.fashion.Catalog.domain.IRepository.IProductRepository;
import io.github.nguyennhatquang.fashion.Catalog.domain.entity.Product;
import io.github.nguyennhatquang.fashion.Catalog.infrastructure.Repository.ProductMongoRepository;
import io.github.nguyennhatquang.fashion.common.Enum.ProductStatusEnum;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryImpl implements IProductRepository {

    private final ProductMongoRepository productMongoRepository;

    @Override
    public Product save(Product product) {
        return productMongoRepository.save(product);
    }

    @Override
    public List<Product> saveAll(List<Product> products) {
        return productMongoRepository.saveAll(products);
    }

    @Override
    public Product update(Product product) {
        return productMongoRepository.save(product);
    }

    @Override
    public List<Product> updateAll(List<Product> products) {
        return productMongoRepository.saveAll(products);
    }

    @Override
    public void delete(Product product) {
        productMongoRepository.delete(product);
    }

    @Override
    public void deleteById(String id) {
        productMongoRepository.deleteById(id);
    }

    @Override
    public void deleteAll(List<Product> products) {
        productMongoRepository.deleteAll(products);
    }

    @Override
    public void softDeleteById(String id) {
        productMongoRepository.findById(id).ifPresent(product -> {
            product.setIsDeleted(true);
            productMongoRepository.save(product);
        });
    }

    @Override
    public void softDeleteBySlug(String slug) {
        productMongoRepository.findBySlug(slug).ifPresent(product -> {
            product.setIsDeleted(true);
            productMongoRepository.save(product);
        });
    }

    @Override
    public void softDeleteAll(List<Product> products) {
        products.forEach(product -> {
            product.setIsDeleted(true);
            productMongoRepository.save(product);
        });
    }

    @Override
    public Optional<Product> findById(String id) {
        return productMongoRepository.findById(id);
    }

    @Override
    public Optional<Product> findByStatus(ProductStatusEnum status) {
        return productMongoRepository.findByStatus(status);
    }

    @Override
    public Optional<Product> findBySlug(String slug) {
        return productMongoRepository.findBySlug(slug);
    }

    @Override
    public List<Product> findAll() {
        return productMongoRepository.findAll();
    }
}
