package io.github.nguyennhatquang.fashion.Catalog.usecase.AdapterUseCase;

import java.util.List;

import org.springframework.stereotype.Service;

import io.github.nguyennhatquang.fashion.Catalog.delivery.Dto.Category.CategoryRequest;
import io.github.nguyennhatquang.fashion.Catalog.delivery.Dto.Category.CategoryResponse;
import io.github.nguyennhatquang.fashion.Catalog.delivery.Mapper.CategoryMapper;
import io.github.nguyennhatquang.fashion.Catalog.domain.IRepository.ICategoryRepository;
import io.github.nguyennhatquang.fashion.Catalog.domain.IRepository.IProductRepository;
import io.github.nguyennhatquang.fashion.Catalog.domain.entity.Category;
import io.github.nguyennhatquang.fashion.Catalog.domain.entity.Product;
import io.github.nguyennhatquang.fashion.Catalog.usecase.IUseCase.IAdminCategoryUseCase;
import io.github.nguyennhatquang.fashion.Catalog.utils.SlugHepler;
import io.github.nguyennhatquang.fashion.common.request.ExactPageRequest;
import io.github.nguyennhatquang.fashion.common.request.PanigationRequest;
import io.github.nguyennhatquang.fashion.common.response.ExactPageResponse;
import io.github.nguyennhatquang.fashion.common.response.PanigationResponse;
import io.github.nguyennhatquang.fashion.common.response.Result;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminCategoryUseCase implements IAdminCategoryUseCase {
    private final ICategoryRepository categoryRepo;
    private final CategoryMapper mapper;
    private final IProductRepository productRepository;

    @Override
    public Result<Category, Exception> createCategory(CategoryRequest.CategoryCreateRequest request) {
        try {
            Category category = mapper.toEntity(request);
            category.setSlug(SlugHepler.generateUniqueSlug(category.getName()));
            if (request.parentId() != null) {
                Category parentCategory = categoryRepo.findById(request.parentId())
                        .orElseThrow(() -> new Exception("Parent category not found"));
                category.setPath(parentCategory.getPath() + "/" + SlugHepler.generateUniqueSlug(category.getName()));
            }
            category = categoryRepo.save(category);
            return Result.success(category);
        } catch (Exception e) {
            return Result.error(e);
        }
    }

    @Override
    public Result<Category, Exception> updateCategory(String id, CategoryRequest.CategoryUpdateRequest request) {
        // TODO Auto-generated method stub
        try {
            Category category = categoryRepo.findById(id).orElseThrow(() -> new Exception("Category not found"));
            mapper.updateEntityFromRequest(request, category);
            category.setSlug(SlugHepler.generateUniqueSlug(category.getName()));
            category = categoryRepo.save(category);
            return Result.success(category);
        } catch (Exception e) {
            return Result.error(e);
        }
    }

    @Override
    public Result<Void, Exception> deleteCategory(String id) {
        // TODO Auto-generated method stub
        try {
            List<Product> products = productRepository.findProductsWithExactlyOneSpecificCategory(id);
            if (products.size() > 0) {
                return Result.error(new Exception("Category has products"));
            }
            categoryRepo.softDeleteById(id);
            return Result.success(null);
        } catch (Exception e) {
            return Result.error(e);
        }
    }

    @Override
    public Result<Category, Exception> getCategoryById(String id) {
        // TODO Auto-generated method stub
        try {
            Category category = categoryRepo.findById(id).orElseThrow(() -> new Exception("Category not found"));
            return Result.success(category);
        } catch (Exception e) {
            return Result.error(e);
        }
    }

    @Override
    public Result<ExactPageResponse<Category>, Exception> GetExactPageResponse(ExactPageRequest request) {
        try {
            ExactPageResponse<Category> response = categoryRepo.getCategoriesExactPage(request);
            return Result.success(response);
        } catch (Exception e) {
            return Result.error(e);
        }
    }

    @Override
    public Result<PanigationResponse<Category>, Exception> getCategorysCursor(PanigationRequest request) {
        try {
            PanigationResponse<Category> response = categoryRepo.getCategoriesCursor(request);
            return Result.success(response);
        } catch (Exception e) {
            return Result.error(e);
        }
    }

}
