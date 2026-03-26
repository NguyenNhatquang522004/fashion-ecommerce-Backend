package io.github.nguyennhatquang.fashion.Catalog.domain.IRepository;

import java.util.List;
import java.util.Optional;
import io.github.nguyennhatquang.fashion.Catalog.domain.entity.Brand;
import io.github.nguyennhatquang.fashion.common.request.ExactPageRequest;
import io.github.nguyennhatquang.fashion.common.request.PanigationRequest;
import io.github.nguyennhatquang.fashion.common.response.ExactPageResponse;
import io.github.nguyennhatquang.fashion.common.response.PanigationResponse;

public interface IBrandRepository {
    Brand save(Brand brand);

    List<Brand> saveAll(List<Brand> brands);

    Brand update(Brand brand);

    List<Brand> updateAll(List<Brand> brands);

    void delete(Brand brand);

    void deleteById(String id);

    void deleteAll(List<Brand> brands);

    void softDeleteById(String id);

    void softDeleteBySlug(String slug);

    Optional<Brand> findById(String id);

    Optional<Brand> findBySlug(String slug);

    List<Brand> findAll();

    ExactPageResponse<Brand> getBrandsExactPage(ExactPageRequest request);

    PanigationResponse<Brand> getBrandsCursor(PanigationRequest request);
}
