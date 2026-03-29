package io.github.nguyennhatquang.fashion.Catalog.usecase.AdapterUseCase;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort; // Nhớ import Sort
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import co.elastic.clients.elasticsearch._types.aggregations.Aggregation; // Import API v8
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import io.github.nguyennhatquang.fashion.Catalog.delivery.Dto.SearchModel.SearchModelRequest;
import io.github.nguyennhatquang.fashion.Catalog.delivery.Dto.SearchModel.SearchModelResponse;
import io.github.nguyennhatquang.fashion.Catalog.domain.entity.SearchModel;
import io.github.nguyennhatquang.fashion.Catalog.usecase.IUseCase.IProductSearchUseCase;
import io.github.nguyennhatquang.fashion.common.response.Result;
import io.github.nguyennhatquang.fashion.common.shared.IRedis;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductSearchUseCase implements IProductSearchUseCase {
    private final IRedis redis;
    private static final String SEARCH_CACHE_PREFIX = "search:catalog:";
    private static final long CACHE_TTL = 10;
    private final ElasticsearchOperations elasticsearchOperations;

    @Override
    public Result<SearchHits<SearchModel>, Exception> searchProducts(SearchModelRequest request) {
        String cacheKey = generateCacheKey(request);
        try {
   
            List<Query> filters = new ArrayList<>();
            filters.add(QueryBuilders.term(t -> t.field("is_active").value(true)));
            filters.add(QueryBuilders.term(t -> t.field("is_deleted").value(false)));

            if (request.getCategoryId() != null && !request.getCategoryId().isBlank()) {
                filters.add(QueryBuilders.term(t -> t.field("categories.id").value(request.getCategoryId())));
            }
            if (request.getMinPrice() != null) {
                filters.add(QueryBuilders
                        .range(r -> r.number(n -> n.field("pricing.min_price").gte(request.getMinPrice()))));
            }
            if (request.getMaxPrice() != null) {
                filters.add(QueryBuilders
                        .range(r -> r.number(n -> n.field("pricing.max_price").lte(request.getMaxPrice()))));
            }

            List<Query> musts = new ArrayList<>();
            List<Query> shoulds = new ArrayList<>();
            boolean hasKeyword = request.getKeyword() != null && !request.getKeyword().isBlank();

            if (hasKeyword) {
                musts.add(QueryBuilders.multiMatch(m -> m
                        .query(request.getKeyword())
                        .fields("name^4", "name.suggest^2", "brand.name^1.5", "categories.name")
                        .fuzziness("AUTO")
                        .minimumShouldMatch("70%")));
                shoulds.add(QueryBuilders.matchPhrase(m -> m.field("name").query(request.getKeyword()).boost(5.0f)));
            }

            Query boolQuery = QueryBuilders.bool(b -> b.filter(filters).must(musts).should(shoulds));

            // 4. Xử lý phân trang
            int pageNumber = (request.getPage() != null && request.getPage() > 0) ? request.getPage() - 1 : 0;
            int pageSize = (request.getSize() != null && request.getSize() > 0) ? request.getSize() : 20;

            // --- BẮT ĐẦU PHẦN CẢI TIẾN 100% BEST PRACTICE ---

            // 5. Dynamic Sorting (Sắp xếp động)
            Sort sortOrder;
            if (hasKeyword) {
                // Nếu có tìm kiếm từ khóa, ưu tiên trả về thằng khớp nhất (score cao nhất)
                sortOrder = Sort.by(Sort.Order.desc("_score"));
            } else {
                // Nếu chỉ lướt xem danh mục (không search text), ưu tiên hàng mới nhất
                sortOrder = Sort.by(Sort.Order.desc("created_at"));
            }

            // 6. Aggregations (Thống kê số lượng theo Brand và Danh mục con)
            // Lưu ý: Ở bản v8+, Aggregation được build qua
            // co.elastic.clients.elasticsearch._types.aggregations.Aggregation
            Aggregation brandAgg = Aggregation.of(a -> a
                    .terms(t -> t.field("brand.name").size(10)) // Đếm top 10 brand xuất hiện trong kết quả
            );

            Aggregation categoryAgg = Aggregation.of(a -> a
                    .terms(t -> t.field("categories.name").size(10)) // Đếm top 10 danh mục
            );

            // 7. Khởi tạo NativeQuery hoàn chỉnh
            NativeQuery nativeQuery = NativeQuery.builder()
                    .withQuery(boolQuery)
                    .withPageable(PageRequest.of(pageNumber, pageSize))
                    .withSort(sortOrder) // Thêm Sắp xếp
                    .withAggregation("brands_count", brandAgg) // Thêm Thống kê Brand
                    .withAggregation("categories_count", categoryAgg) // Thêm Thống kê Danh mục
                    .build();

            // 8. Thực thi tìm kiếm
            SearchHits<SearchModel> hits = elasticsearchOperations.search(nativeQuery, SearchModel.class);
            return Result.success(hits);

        } catch (Exception e) {
            log.error("Lỗi Elasticsearch query: {}", e.getMessage(), e);
            return Result.error(e);
        }
    }

    public String generateCacheKey(SearchModelRequest request) {
        String rawKey = String.format("k:%s-c:%s-pmin:%s-pmax:%s-p:%d-s:%d",
                request.getKeyword(),
                request.getCategoryId(),
                request.getMinPrice(),
                request.getMaxPrice(),
                request.getPage(),
                request.getSize());

        // Dùng MD5 để rút gọn key nếu cần, hoặc để nguyên để dễ debug
        return SEARCH_CACHE_PREFIX + DigestUtils.md5DigestAsHex(rawKey.getBytes());
    }
}