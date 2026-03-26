package io.github.nguyennhatquang.fashion.Catalog.usecase.IUseCase;

import java.time.Instant;

import io.github.nguyennhatquang.fashion.Catalog.domain.entity.Product;
import io.github.nguyennhatquang.fashion.common.response.Result;

public interface IAdminProductUseCase {
    Result<Product, Exception> createProduct(Product product);

    Result<Product, Exception> updateProduct(Product product);

    Result<Product, Exception> deleteProduct(Product product);

    Result<Product, Exception> getProductById(String id);

    Result<Product, Exception> getProductByName(String name);

    Result<Product, Exception> getProductBySlug(String slug);

    Result<Product, Exception> getProductByIsActive(Boolean isActive);

    Result<Product, Exception> getProductByIsDeleted(Boolean isDeleted);

    Result<Product, Exception> getProductByCreatedAt(Instant createdAt);

    Result<Product, Exception> getProductByUpdatedAt(Instant updatedAt);

    Result<Product, Exception> getProductByCreatedBy(String createdBy);

    Result<Product, Exception> getProductByUpdatedBy(String updatedBy);
}
