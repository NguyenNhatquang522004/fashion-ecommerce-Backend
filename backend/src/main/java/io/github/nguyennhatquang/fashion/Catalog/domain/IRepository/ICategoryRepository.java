package io.github.nguyennhatquang.fashion.Catalog.domain.IRepository;

import java.util.List;
import java.util.Optional;


import io.github.nguyennhatquang.fashion.Catalog.domain.entity.Category;
import io.github.nguyennhatquang.fashion.common.request.ExactPageRequest;
import io.github.nguyennhatquang.fashion.common.request.PanigationRequest;
import io.github.nguyennhatquang.fashion.common.response.ExactPageResponse;
import io.github.nguyennhatquang.fashion.common.response.PanigationResponse;

public interface ICategoryRepository {
    Category save(Category category);

    List<Category> saveAll(List<Category> categories);

    Category update(Category category);

    List<Category> updateAll(List<Category> categories);

    void delete(Category category);

    void deleteById(String id);

    void deleteAll(List<Category> categories);

    void softDeleteById(String id);

    void softDeleteBySlug(String slug);

    void softDeleteByParentId(String parentId);

    Optional<Category> findById(String id);

    Optional<Category> findBySlug(String slug);

    Optional<List<Category>> findByParentId(String parentId);

    List<Category> findAll();

    ExactPageResponse<Category> getCategoriesExactPage(ExactPageRequest request);

    PanigationResponse<Category> getCategoriesCursor(PanigationRequest request);

}
