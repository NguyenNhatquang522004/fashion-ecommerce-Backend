package io.github.nguyennhatquang.fashion.Catalog.delivery.Handler;

import java.math.BigDecimal;

import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping; // Đã bổ sung import bị thiếu
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.github.nguyennhatquang.fashion.Catalog.delivery.Dto.SearchModel.SearchModelRequest;
import io.github.nguyennhatquang.fashion.Catalog.delivery.Dto.SearchModel.SearchModelResponse;
import io.github.nguyennhatquang.fashion.Catalog.delivery.Mapper.ProductSearchMapper;
import io.github.nguyennhatquang.fashion.Catalog.domain.entity.SearchModel;
import io.github.nguyennhatquang.fashion.Catalog.usecase.IUseCase.IProductSearchUseCase;
import io.github.nguyennhatquang.fashion.common.response.Result;
import io.github.nguyennhatquang.fashion.common.response.SystemRes;
import io.github.nguyennhatquang.fashion.common.shared.IRedis;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
@Slf4j
public class SearchHandler {
    private final IRedis redis;
    private final IProductSearchUseCase productSearchUseCase;

    // FIX: Sửa lại đúng tên Class (Viết hoa chữ cái đầu)
    private final ProductSearchMapper searchMapper;

    @GetMapping
    public ResponseEntity<SystemRes> searchProducts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String categoryId,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            // FIX: Chuyển đổi BigDecimal sang Double an toàn
            SearchModelRequest request = SearchModelRequest.builder()
                    .keyword(keyword)
                    .categoryId(categoryId)
                    .minPrice(minPrice != null ? minPrice.doubleValue() : null)
                    .maxPrice(maxPrice != null ? maxPrice.doubleValue() : null)
                    .page(page)
                    .size(size)
                    .build();
            String cacheKey = productSearchUseCase.generateCacheKey(request);
            SearchModelResponse cachedResponse = redis.get(cacheKey, SearchModelResponse.class);
            if (cachedResponse != null) {
                log.info("Cache Hit for key: {}", cacheKey);
                return ResponseEntity.ok().body(
                        SystemRes.builder().status("200").message("Search success").data(cachedResponse).build());
            }

            log.info("Cache Miss for key: {}. Querying Elasticsearch...", cacheKey);
            Result<SearchHits<SearchModel>, Exception> result = productSearchUseCase.searchProducts(request);
            if (result.hasError()) {
                return ResponseEntity.badRequest().body(
                        SystemRes.builder().status("400").message(result.error().getMessage()).data(null).build());
            }
            SearchHits<SearchModel> hits = result.data();

            // FIX: Truyền thêm tham số page và size cho Mapper
            SearchModelResponse response = searchMapper.toResponse(hits, page, size);

            return ResponseEntity.ok().body(
                    SystemRes.builder().status("200").message("Search success").data(response).build());
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(SystemRes.builder().status("400").message(e.getMessage()).data(null).build());
        }
    }
}