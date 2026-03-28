package io.github.nguyennhatquang.fashion.Catalog.usecase.AdapterUseCase;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import io.github.nguyennhatquang.fashion.Catalog.delivery.Dto.Product.ProductRequest.ProductCreateRequest;
import io.github.nguyennhatquang.fashion.Catalog.delivery.Dto.Product.ProductRequest.ProductUpdateRequest;
import io.github.nguyennhatquang.fashion.Catalog.delivery.Mapper.ProductMapper;
import io.github.nguyennhatquang.fashion.Catalog.domain.IRepository.IBrandRepository;
import io.github.nguyennhatquang.fashion.Catalog.domain.IRepository.IProductRepository;
import io.github.nguyennhatquang.fashion.Catalog.domain.IRepository.ISkuVariantRepository;
import io.github.nguyennhatquang.fashion.Catalog.domain.entity.Brand;
import io.github.nguyennhatquang.fashion.Catalog.domain.entity.Product;
import io.github.nguyennhatquang.fashion.Catalog.domain.entity.SkuVariant;
import io.github.nguyennhatquang.fashion.Catalog.usecase.IUseCase.IAdminProductUseCase;
import io.github.nguyennhatquang.fashion.Catalog.utils.SlugHepler;
import io.github.nguyennhatquang.fashion.common.request.ExactPageRequest;
import io.github.nguyennhatquang.fashion.common.request.PanigationRequest;
import io.github.nguyennhatquang.fashion.common.response.ExactPageResponse;
import io.github.nguyennhatquang.fashion.common.response.PanigationResponse;
import io.github.nguyennhatquang.fashion.common.response.Result;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminProductUseCase implements IAdminProductUseCase {
    private final IBrandRepository brandRepository;
    private final IProductRepository productRepository;
    private final ProductMapper mapper;
    private final ISkuVariantRepository skuVariantRepository;

    @Override
    public Result<Product, Exception> createProduct(ProductCreateRequest request) {
        try {
            Optional<Brand> brand = brandRepository.findById(request.brandId());
            if (brand.isEmpty()) {
                return Result.error(new Exception("Brand not found"));
            }
            Product product = mapper.toEntity(request);
            product.setBrandName(brand.get().getName());
            product.setSlug(SlugHepler.generateUniqueSlug(product.getName()));
            productRepository.save(product);
            return Result.success(product);
        } catch (Exception e) {
            return Result.error(e);
        }
    }

    @Override
    public Result<Product, Exception> updateProduct(String Id, ProductUpdateRequest request) {
        // TODO Auto-generated method stub
        try {
            Product product = productRepository.findById(Id).orElseThrow(() -> new Exception("Product not found"));
            mapper.updateEntityFromRequest(request, product);
            product.setSlug(SlugHepler.generateUniqueSlug(product.getName()));
            productRepository.save(product);
            return Result.success(product);
        } catch (Exception e) {
            return Result.error(e);
        }
    }

    @Override
    public Result<Product, Exception> deleteProductById(String Id) {
        // TODO Auto-generated method stub
        try {
            List<SkuVariant> skuVariants = skuVariantRepository.findByProductId(Id);
            if (skuVariants.size() > 0) {
                return Result.error(new Exception("Product has sku variants"));
            }
            productRepository.softDeleteById(Id);
            return Result.success(null);
        } catch (Exception e) {
            return Result.error(e);
        }
    }

    @Override
    public Result<Product, Exception> getProductById(String id) {
        // TODO Auto-generated method stub
        try {
            Product product = productRepository.findById(id).orElseThrow(() -> new Exception("Product not found"));
            return Result.success(product);
        } catch (Exception e) {
            return Result.error(e);
        }
    }

    @Override
    public Result<Product, Exception> getProductBySlug(String slug) {
        // TODO Auto-generated method stub
        try {
            Product product = productRepository.findBySlug(slug).orElseThrow(() -> new Exception("Product not found"));
            return Result.success(product);
        } catch (Exception e) {
            return Result.error(e);
        }
    }

    @Override
    public Result<ExactPageResponse<Product>, Exception> GetExactPageResponse(ExactPageRequest request) {
        // TODO Auto-generated method stub
        try {
            ExactPageResponse<Product> response = productRepository.getProductsExactPage(request);
            return Result.success(response);
        } catch (Exception e) {
            return Result.error(e);
        }
    }

    @Override
    public Result<PanigationResponse<Product>, Exception> getBrandsCursor(PanigationRequest request) {
        // TODO Auto-generated method stub
        try {
            PanigationResponse<Product> response = productRepository.getProductsCursor(request);
            return Result.success(response);
        } catch (Exception e) {
            return Result.error(e);
        }
    }

}
