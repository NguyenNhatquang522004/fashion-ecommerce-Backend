package io.github.nguyennhatquang.fashion.Catalog.infrastructure.Repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import io.github.nguyennhatquang.fashion.Catalog.domain.entity.Brand;

import java.util.Optional;

@Repository
public interface BrandMongoRepository extends MongoRepository<Brand, String> {
    Optional<Brand> findBySlug(String slug);
}
