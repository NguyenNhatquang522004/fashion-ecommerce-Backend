package io.github.nguyennhatquang.fashion.common.infrastructure.Function;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.github.nguyennhatquang.fashion.common.Utils.CursorUtils;
import io.github.nguyennhatquang.fashion.common.request.CursorData;
import io.github.nguyennhatquang.fashion.common.request.PaginationRequestv2;
import io.github.nguyennhatquang.fashion.common.response.PanigationResponse;

@Service
@RequiredArgsConstructor
public class MongoCursorPaginationService {

    private final MongoTemplate mongoTemplate;
    private final CursorUtils cursorCodec;

    public <T> PanigationResponse<T> execute(
            PaginationRequestv2 request,
            Class<T> entityClass,
            String collectionName,
            Set<String> allowedSortFields,
            Set<String> allowedFilterFields) {

        int limit = request.getLimit() != null ? request.getLimit() : 10;
        String sortBy = request.getSortBy();
        boolean isDesc = "DESC".equalsIgnoreCase(request.getSortDirection());
        boolean isFetchingNext = request.getHasNext() != null && request.getHasNext();

        // --- 1. VALIDATE BẢO MẬT (CHUẨN BEST PRACTICE) ---
        if (!allowedSortFields.contains(sortBy)) {
            throw new IllegalArgumentException("Hệ thống không hỗ trợ sắp xếp theo trường: " + sortBy);
        }

        Query query = new Query();

        // --- 2. VALIDATE VÀ BUILD FILTER ĐỘNG ---
        if (request.getFilters() != null && !request.getFilters().isEmpty()) {
            for (Map.Entry<String, Object> entry : request.getFilters().entrySet()) {
                String filterKey = entry.getKey();
                Object filterValue = entry.getValue();

                // Chặn đứng nếu client filter những trường không được phép
                if (!allowedFilterFields.contains(filterKey)) {
                    throw new IllegalArgumentException("Hệ thống không hỗ trợ lọc theo trường: " + filterKey);
                }

                // TỰ ĐỘNG NHẬN DIỆN KIỂU DỮ LIỆU ĐỂ DÙNG TOÁN TỬ CHO CHUẨN
                if (filterValue instanceof java.util.Collection<?>) {
                    // Nếu Client gửi lên một List (Ví dụ: ["a", "b"]) -> Dùng toán tử $in
                    query.addCriteria(Criteria.where(filterKey).in((java.util.Collection<?>) filterValue));
                } else {
                    // Nếu Client gửi lên 1 giá trị đơn (Ví dụ: "a") -> Dùng toán tử bằng ($eq)
                    query.addCriteria(Criteria.where(filterKey).is(filterValue));
                }
            }
        }

        // Mặc định luôn thêm điều kiện hệ thống (Client không can thiệp được)
        query.addCriteria(Criteria.where("isDeleted").is(false));

        // --- 3. XỬ LÝ CURSOR ---
        if (request.getCursor() != null) {
            CursorData cursorData = cursorCodec.decodeCursorV2(request.getCursor());
            if (cursorData != null) {
                Object cursorValue = cursorData.getSortValue();
                String cursorId = cursorData.getId();

                Criteria cursorCriteria = buildCursorCriteria(sortBy, cursorValue, cursorId, isDesc, isFetchingNext);
                query.addCriteria(cursorCriteria);
            }
        }

        // --- 4. SẮP XẾP VÀ TRUY VẤN ---
        Sort.Direction primaryDirection = isDesc ? Sort.Direction.DESC : Sort.Direction.ASC;
        if (!isFetchingNext && request.getCursor() != null) {
            primaryDirection = isDesc ? Sort.Direction.ASC : Sort.Direction.DESC;
        }

        query.with(Sort.by(primaryDirection, sortBy).and(Sort.by(primaryDirection, "id")));
        query.limit(limit + 1);

        List<T> data = mongoTemplate.find(query, entityClass, collectionName);

        // --- 5. LOGIC PHÂN TRANG NEXT/PREV ---
        boolean hasMore = data.size() > limit;
        if (!isFetchingNext && request.getCursor() != null) {
            Collections.reverse(data);
        }

        if (hasMore) {
            if (isFetchingNext || request.getCursor() == null) {
                data.remove(data.size() - 1);
            } else {
                data.remove(0);
            }
        }

        // --- 6. TẠO CURSOR MỚI AN TOÀN ---
        String nextCursor = null;
        if (!data.isEmpty()) {
            T lastItem = data.get(data.size() - 1);

            BeanWrapper beanWrapper = PropertyAccessorFactory.forBeanPropertyAccess(lastItem);
            // Vì ta đã chặn sortBy ở bước 1, bước này CHẮC CHẮN không bị lỗi
            // NotReadablePropertyException
            Object lastSortValue = beanWrapper.getPropertyValue(sortBy);
            String lastId = String.valueOf(beanWrapper.getPropertyValue("id"));

            nextCursor = cursorCodec.encodeCursorV2(new CursorData(lastSortValue, lastId));
        }

        boolean responseHasNext = (request.getCursor() == null || isFetchingNext) ? hasMore : true;
        boolean responseHasPrevious = (request.getCursor() == null) ? false : (isFetchingNext ? true : hasMore);

        return PanigationResponse.<T>builder()
                .cursor(nextCursor)
                .limit(limit)
                .sort(request.getSortDirection())
                .hasNext(responseHasNext)
                .hasPrevious(responseHasPrevious)
                .data(data)
                .build();
    }

    private Criteria buildCursorCriteria(String sortBy, Object cursorValue, String cursorId, boolean isDesc,
            boolean isFetchingNext) {
        boolean isLessThan = (isDesc && isFetchingNext) || (!isDesc && !isFetchingNext);
        if (isLessThan) {
            return new Criteria().orOperator(
                    Criteria.where(sortBy).lt(cursorValue),
                    new Criteria().andOperator(Criteria.where(sortBy).is(cursorValue),
                            Criteria.where("id").lt(cursorId)));
        } else {
            return new Criteria().orOperator(
                    Criteria.where(sortBy).gt(cursorValue),
                    new Criteria().andOperator(Criteria.where(sortBy).is(cursorValue),
                            Criteria.where("id").gt(cursorId)));
        }
    }
}