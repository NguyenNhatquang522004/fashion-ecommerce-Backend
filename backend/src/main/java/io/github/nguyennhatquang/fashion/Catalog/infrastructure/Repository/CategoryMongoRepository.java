package io.github.nguyennhatquang.fashion.Catalog.infrastructure.Repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import io.github.nguyennhatquang.fashion.Catalog.domain.entity.Category;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryMongoRepository extends MongoRepository<Category, String> {
    Optional<Category> findBySlug(String slug);

    Optional<List<Category>> findByParentId(String parentId);
}
