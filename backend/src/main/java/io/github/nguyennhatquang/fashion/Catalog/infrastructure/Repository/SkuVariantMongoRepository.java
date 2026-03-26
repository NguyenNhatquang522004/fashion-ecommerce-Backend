package io.github.nguyennhatquang.fashion.Catalog.infrastructure.Repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import io.github.nguyennhatquang.fashion.Catalog.domain.entity.SkuVariant;
import java.util.List;
import java.util.Optional;

@Repository
public interface SkuVariantMongoRepository extends MongoRepository<SkuVariant, String> {
    Optional<SkuVariant> findBySkuCode(String skuCode);

    List<SkuVariant> findByProductId(String productId);

    List<SkuVariant> findByProductIdAndIsActiveTrue(String productId);

    List<SkuVariant> findByProductIdAndIsDeletedFalse(String productId);

    List<SkuVariant> findByBarcode(String barcode);

}
