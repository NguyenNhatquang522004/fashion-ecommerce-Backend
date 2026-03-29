package io.github.nguyennhatquang.fashion.Catalog.usecase.IUseCase;

import java.util.List;

import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;

import io.github.nguyennhatquang.fashion.Catalog.delivery.Dto.SearchModel.SearchModelRequest;
import io.github.nguyennhatquang.fashion.Catalog.domain.entity.SearchModel;
import io.github.nguyennhatquang.fashion.common.request.PaginationRequestv2;
import io.github.nguyennhatquang.fashion.common.response.PanigationResponse;
import io.github.nguyennhatquang.fashion.common.response.Result;

public interface IProductSearchUseCase {
    Result<SearchHits<SearchModel>, Exception> searchProducts(SearchModelRequest request);

    String generateCacheKey(SearchModelRequest request);
}
