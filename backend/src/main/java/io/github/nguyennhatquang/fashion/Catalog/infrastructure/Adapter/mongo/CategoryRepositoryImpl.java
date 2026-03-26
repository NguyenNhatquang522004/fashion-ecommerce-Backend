package io.github.nguyennhatquang.fashion.Catalog.infrastructure.Adapter.mongo;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import io.github.nguyennhatquang.fashion.Catalog.domain.IRepository.ICategoryRepository;
import io.github.nguyennhatquang.fashion.Catalog.domain.entity.Category;
import io.github.nguyennhatquang.fashion.Catalog.infrastructure.Repository.CategoryMongoRepository;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CategoryRepositoryImpl implements ICategoryRepository {

    private final CategoryMongoRepository categoryMongoRepository;

    @Override
    public Category save(Category category) {
        return categoryMongoRepository.save(category);
    }

    @Override
    public List<Category> saveAll(List<Category> categories) {
        return categoryMongoRepository.saveAll(categories);
    }

    @Override
    public Category update(Category category) {
        return categoryMongoRepository.save(category);
    }

    @Override
    public List<Category> updateAll(List<Category> categories) {
        return categoryMongoRepository.saveAll(categories);
    }

    @Override
    public void delete(Category category) {
        categoryMongoRepository.delete(category);
    }

    @Override
    public void deleteById(String id) {
        categoryMongoRepository.deleteById(id);
    }

    @Override
    public void deleteAll(List<Category> categories) {
        categoryMongoRepository.deleteAll(categories);
    }

    @Override
    public void softDeleteById(String id) {
        categoryMongoRepository.findById(id).ifPresent(category -> {
            category.setIsDeleted(true);
            categoryMongoRepository.save(category);
        });
    }

    @Override
    public void softDeleteBySlug(String slug) {
        categoryMongoRepository.findBySlug(slug).ifPresent(category -> {
            category.setIsDeleted(true);
            categoryMongoRepository.save(category);
        });
    }

    @Override
    public void softDeleteByParentId(String parentId) {
        categoryMongoRepository.findByParentId(parentId).ifPresent(categories -> {
            categories.forEach(category -> {
                category.setIsDeleted(true);
                categoryMongoRepository.save(category);
            });
        });
    }
    @Override
    public Optional<Category> findById(String id) {
        return categoryMongoRepository.findById(id);
    }

    @Override
    public Optional<Category> findBySlug(String slug) {
        return categoryMongoRepository.findBySlug(slug);
    }

    @Override
    public List<Category> findAll() {
        return categoryMongoRepository.findAll();
    }

    @Override
    public Optional<List<Category>> findByParentId(String parentId) {
        return categoryMongoRepository.findByParentId(parentId);
    }
}
