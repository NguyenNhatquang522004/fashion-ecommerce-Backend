package io.github.nguyennhatquang.fashion.Catalog.usecase.AdapterUseCase;

import io.github.nguyennhatquang.fashion.common.kafka.EventPublisher;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.MDC;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.nguyennhatquang.fashion.Catalog.delivery.Dto.Brand.BrandRequest.BrandCreateRequest;
import io.github.nguyennhatquang.fashion.Catalog.delivery.Dto.Brand.BrandRequest.BrandUpdateRequest;
import io.github.nguyennhatquang.fashion.Catalog.delivery.Mapper.BrandMapper;
import io.github.nguyennhatquang.fashion.Catalog.domain.IRepository.IBrandRepository;
import io.github.nguyennhatquang.fashion.Catalog.domain.IRepository.IProductRepository;
import io.github.nguyennhatquang.fashion.Catalog.domain.IRepository.ISkuVariantRepository;
import io.github.nguyennhatquang.fashion.Catalog.domain.entity.Brand;
import io.github.nguyennhatquang.fashion.Catalog.domain.entity.Product;
import io.github.nguyennhatquang.fashion.Catalog.usecase.IUseCase.IAdminBrandUseCase;
import io.github.nguyennhatquang.fashion.Catalog.utils.SlugHepler;
import io.github.nguyennhatquang.fashion.common.Enum.EventTopic;
import io.github.nguyennhatquang.fashion.common.Enum.EventType;
import io.github.nguyennhatquang.fashion.common.Payload.Category.DeleteBrandPayload;
import io.github.nguyennhatquang.fashion.common.kafka.EventContext;
import io.github.nguyennhatquang.fashion.common.kafka.IntegrationEvent;
import io.github.nguyennhatquang.fashion.common.request.ExactPageRequest;
import io.github.nguyennhatquang.fashion.common.request.PanigationRequest;
import io.github.nguyennhatquang.fashion.common.response.ExactPageResponse;
import io.github.nguyennhatquang.fashion.common.response.PanigationResponse;
import io.github.nguyennhatquang.fashion.common.response.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminBrandUseCase implements IAdminBrandUseCase {
    private final IBrandRepository brandRepository;
    private final BrandMapper brandMapper;
    private final ISkuVariantRepository skuVariantRepository;
    private final ObjectMapper objectMapper;
    private final IProductRepository productRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final EventPublisher eventPublisher;

    @Override
    public Result<Brand, Exception> createBrand(BrandCreateRequest request) {
        try {
            Brand brand = brandMapper.toEntity(request);
            brand.setSlug(SlugHepler.generateUniqueSlug(brand.getName()));
            brandRepository.save(brand);
            return Result.success(brand);
        } catch (Exception e) {
            return Result.error(e);
        }
    }

    @Override
    public Result<Brand, Exception> updateBrand(BrandUpdateRequest request, String id) {
        try {
            Brand brand = brandRepository.findById(id).orElse(null);
            if (brand == null) {
                return Result.error(new Exception("Brand not found"));
            }
            brandMapper.updateEntityFromRequest(request, brand);
            brand.setSlug(SlugHepler.generateUniqueSlug(brand.getName()));
            brandRepository.save(brand);
            return Result.success(brand);
        } catch (Exception e) {
            return Result.error(e);
        }
    }

    @Override
    public Result<Void, Exception> deleteBrand(EventContext ctx, String id) {
        try {
            List<Product> products = productRepository.findProductsWithExactlyOneSpecificBrand(id);
            if (products.size() > 0) {
                for (Product item : products) {
                    Long count = skuVariantRepository.countByProductId(item.getId());
                    if (count > 0) {
                        return Result.error(new Exception("Brand has products"));
                    }
                }
            } else {
                ctx.throwIfExpired();
                DeleteBrandPayload payload = DeleteBrandPayload.builder().BrandID(id).build();
                eventPublisher.publish(EventTopic.BRAND_DELETE, EventType.DELETED, ctx, id, payload);
            }
            brandRepository.softDeleteById(id);
            return Result.success(null);
        } catch (Exception e) {
            return Result.error(e);
        }
    }

    @Override
    public Result<Brand, Exception> getBrandById(String id) {
        try {
            List<Product> products = productRepository.findProductsWithExactlyOneSpecificBrand(id);
            if (products.size() > 0) {
                return Result.error(new Exception("Brand has products"));
            }
            Brand brand = brandRepository.findById(id).orElse(null);
            if (brand == null) {
                return Result.error(new Exception("Brand not found"));
            }
            return Result.success(brand);
        } catch (Exception e) {
            return Result.error(e);
        }
    }

    @Override
    public Result<ExactPageResponse<Brand>, Exception> GetExactPageResponse(ExactPageRequest request) {
        try {
            ExactPageResponse<Brand> data = brandRepository.getBrandsExactPage(request);
            if (data == null) {
                return null;
            }
            return Result.success(data);
        } catch (Exception e) {
            return Result.error(e);
        }
    }

    @Override
    public Result<PanigationResponse<Brand>, Exception> getBrandsCursor(PanigationRequest request) {
        try {
            PanigationResponse<Brand> data = brandRepository.getBrandsCursor(request);
            if (data == null) {
                return null;
            }
            return Result.success(data);
        } catch (Exception e) {
            return Result.error(e);
        }
    }

    @Override
    public Result<List<Brand>, Exception> getAllBrand() {
        try {
            List<Brand> data = brandRepository.findAll();
            if (data == null) {
                return null;
            }
            return Result.success(data);
        } catch (Exception e) {
            return Result.error(e);
        }
    }
}
