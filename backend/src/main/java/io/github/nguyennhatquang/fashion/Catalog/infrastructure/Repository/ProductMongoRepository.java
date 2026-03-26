package io.github.nguyennhatquang.fashion.Catalog.infrastructure.Repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import io.github.nguyennhatquang.fashion.Catalog.domain.entity.Product;
import io.github.nguyennhatquang.fashion.common.Enum.ProductStatusEnum;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductMongoRepository extends MongoRepository<Product, String> {
    Optional<Product> findBySlug(String slug);

    Optional<Product> findByStatus(ProductStatusEnum status);

    List<Product> findByCategoryIdsContainingAndIsDeletedFalse(String categoryId);

    Optional<Product> findByBrandId(String brandId);
}
