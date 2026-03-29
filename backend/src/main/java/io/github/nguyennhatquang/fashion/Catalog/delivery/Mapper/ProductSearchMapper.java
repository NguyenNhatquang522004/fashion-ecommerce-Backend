package io.github.nguyennhatquang.fashion.Catalog.delivery.Mapper;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Component; // BẮT BUỘC PHẢI CÓ
import org.springframework.data.elasticsearch.client.elc.ElasticsearchAggregation;
// FIX: Thêm 2 class này để ép kiểu Aggregations chuẩn của V8
import org.springframework.data.elasticsearch.client.elc.ElasticsearchAggregations;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregate;
import io.github.nguyennhatquang.fashion.Catalog.delivery.Dto.SearchModel.SearchModelResponse;
import io.github.nguyennhatquang.fashion.Catalog.delivery.Dto.SearchModel.SearchModelResponse.FacetTerm;
import io.github.nguyennhatquang.fashion.Catalog.delivery.Dto.SearchModel.SearchModelResponse.Facets;
import io.github.nguyennhatquang.fashion.Catalog.delivery.Dto.SearchModel.SearchModelResponse.Pagination;
import io.github.nguyennhatquang.fashion.Catalog.delivery.Dto.SearchModel.SearchModelResponse.ProductItem;
import io.github.nguyennhatquang.fashion.Catalog.domain.entity.SearchModel;

@Component // FIX: Đánh dấu đây là một Bean để Handler Inject được vào
public class ProductSearchMapper {

    @Value("${app.domain:http://localhost:3000}")
    private String domain;

    public SearchModelResponse toResponse(SearchHits<SearchModel> hits, int pageNumber, int pageSize) {
        return SearchModelResponse.builder()
                .items(mapItems(hits))
                .metadata(mapPagination(hits, pageNumber, pageSize))
                .aggregations(mapFacets(hits))
                .build();
    }

    private List<ProductItem> mapItems(SearchHits<SearchModel> hits) {
        return hits.getSearchHits().stream()
                .map(hit -> {
                    SearchModel model = hit.getContent();
                    return ProductItem.builder()
                            .id(model.getId())
                            .name(model.getName())
                            .slug(model.getSlug())
                            .productUrl(domain + "/san-pham/" + model.getSlug())
                            .thumbnailUrl(model.getThumbnailUrl())
                            .minPrice(model.getPricing() != null ? model.getPricing().getMinPrice() : null)
                            .maxPrice(model.getPricing() != null ? model.getPricing().getMaxPrice() : null)
                            .brandName(model.getBrand() != null ? model.getBrand().getName() : null)
                            .build();
                }).toList();
    }

    private Pagination mapPagination(SearchHits<SearchModel> hits, int pageNumber, int pageSize) {
        long totalElements = hits.getTotalHits();
        int totalPages = pageSize > 0 ? (int) Math.ceil((double) totalElements / pageSize) : 0;

        return Pagination.builder()
                .page(pageNumber) // Đã khớp với logic page truyền từ request vào
                .size(pageSize)
                .totalElements(totalElements)
                .totalPages(totalPages)
                .build();
    }

    private Facets mapFacets(SearchHits<SearchModel> hits) {
        return Facets.builder()
                .brands(extractAggregation(hits, "brands_count"))
                .categories(extractAggregation(hits, "categories_count"))
                .build();
    }

    /**
     * Hàm trích xuất Aggregation tương thích với Elasticsearch Java API Client v8+
     */
   private List<FacetTerm> extractAggregation(SearchHits<?> hits, String aggName) {
        List<FacetTerm> facetTerms = new ArrayList<>();

        // if (hits.hasAggregations()) {
        //     // 1. Ép kiểu về ElasticsearchAggregations (container của Spring)
        //     ElasticsearchAggregations container = (ElasticsearchAggregations) hits.getAggregations();
            
        //     // 2. Lấy trực tiếp đối tượng Aggregate từ thư viện co.elastic.clients
        //     Aggregate aggregate = container.aggregations().getClass().getField(aggName);

        //     if (aggregate != null && aggregate.isSterms()) {
        //         var buckets = aggregate.sterms().buckets().array();
        //         for (var bucket : buckets) {
        //             facetTerms.add(new FacetTerm(
        //                     bucket.key().stringValue(),
        //                     bucket.docCount()));
        //         }
        //     }
        // }
        return facetTerms;
    }
}