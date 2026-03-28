package io.github.nguyennhatquang.fashion.Catalog.domain.IRepository;

import java.util.List;
import java.util.Optional;

import io.github.nguyennhatquang.fashion.Catalog.domain.entity.Brand;
import io.github.nguyennhatquang.fashion.Catalog.domain.entity.Product;
import io.github.nguyennhatquang.fashion.common.Enum.ProductStatusEnum;
import io.github.nguyennhatquang.fashion.common.request.ExactPageRequest;
import io.github.nguyennhatquang.fashion.common.request.PanigationRequest;
import io.github.nguyennhatquang.fashion.common.response.ExactPageResponse;
import io.github.nguyennhatquang.fashion.common.response.PanigationResponse;

public interface IProductRepository {
    Product save(Product product);

    List<Product> saveAll(List<Product> products);

    Product update(Product product);

    List<Product> updateAll(List<Product> products);

    void delete(Product product);

    void deleteById(String id);

    void deleteAll(List<Product> products);

    void softDeleteById(String id);

    void softDeleteBySlug(String slug);

    void softDeleteAll(List<Product> products);

    Optional<Product> findById(String id);

    Optional<Product> findBySlug(String slug);

    Optional<Product> findByStatus(ProductStatusEnum status);

    List<Product> findAll();

    List<Product> findProductsWithExactlyOneSpecificBrand(String brandId);

    List<Product> findProductsWithExactlyOneSpecificCategory(String categoryId);

    ExactPageResponse<Product> getProductsExactPage(ExactPageRequest request);

    PanigationResponse<Product> getProductsCursor(PanigationRequest request);
}
