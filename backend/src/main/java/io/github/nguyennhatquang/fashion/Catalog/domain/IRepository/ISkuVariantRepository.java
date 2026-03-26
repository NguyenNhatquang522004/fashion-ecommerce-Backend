package io.github.nguyennhatquang.fashion.Catalog.domain.IRepository;

import java.util.List;
import java.util.Optional;

import io.github.nguyennhatquang.fashion.Catalog.domain.entity.Brand;
import io.github.nguyennhatquang.fashion.Catalog.domain.entity.SkuVariant;
import io.github.nguyennhatquang.fashion.common.request.ExactPageRequest;
import io.github.nguyennhatquang.fashion.common.request.PanigationRequest;
import io.github.nguyennhatquang.fashion.common.response.ExactPageResponse;
import io.github.nguyennhatquang.fashion.common.response.PanigationResponse;

public interface ISkuVariantRepository {
    SkuVariant save(SkuVariant skuVariant);

    List<SkuVariant> saveAll(List<SkuVariant> skuVariants);

    SkuVariant update(SkuVariant skuVariant);

    List<SkuVariant> updateAll(List<SkuVariant> skuVariants);

    void delete(SkuVariant skuVariant);

    void deleteById(String id);

    void softDeleteById(String id);

    void softDeleteBySkuCode(String skuCode);

    void softDeleteByProductId(String productId);

    void deleteAll(List<SkuVariant> skuVariants);

    Optional<SkuVariant> findById(String id);

    Optional<SkuVariant> findBySkuCode(String skuCode);

    List<SkuVariant> findByProductId(String productId);

    List<SkuVariant> findByProductIdAndIsActiveTrue(String productId);

    List<SkuVariant> findByProductIdAndIsDeletedFalse(String productId);

    List<SkuVariant> findByBarcode(String barcode);

    List<SkuVariant> findAll();

    ExactPageResponse<SkuVariant> getSkuVariantsExactPage(ExactPageRequest request);

    PanigationResponse<SkuVariant> getSkuVariantsCursor(PanigationRequest request);
}
