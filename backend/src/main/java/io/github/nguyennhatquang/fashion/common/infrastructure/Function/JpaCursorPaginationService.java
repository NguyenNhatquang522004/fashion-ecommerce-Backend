package io.github.nguyennhatquang.fashion.common.infrastructure.Function;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

// Bỏ import java.util.function.Predicate; đi
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.stereotype.Service;

import io.github.nguyennhatquang.fashion.common.Utils.CursorUtils;
import io.github.nguyennhatquang.fashion.common.request.CursorData;
import io.github.nguyennhatquang.fashion.common.request.PaginationRequestv2;
import io.github.nguyennhatquang.fashion.common.response.PanigationResponse;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate; // Import đúng Predicate của JPA
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JpaCursorPaginationService {
    private final EntityManager entityManager;
    private final CursorUtils cursorCodec;

    public <T> PanigationResponse<T> execute(
            PaginationRequestv2 request,
            Class<T> entityClass,
            Set<String> allowedSortFields,
            Set<String> allowedFilterFields) {

        int limit = request.getLimit() != null ? request.getLimit() : 10;
        String sortBy = request.getSortBy();
        boolean isDesc = "DESC".equalsIgnoreCase(request.getSortDirection());
        boolean isFetchingNext = request.getHasNext() != null && request.getHasNext();

        // --- 1. VALIDATE BẢO MẬT ---
        if (!allowedSortFields.contains(sortBy)) {
            throw new IllegalArgumentException("Hệ thống không hỗ trợ sắp xếp theo trường: " + sortBy);
        }

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> cq = cb.createQuery(entityClass);
        Root<T> root = cq.from(entityClass);
        List<Predicate> predicates = new ArrayList<>();

        // --- 2. VALIDATE VÀ BUILD FILTER ĐỘNG ---
        if (request.getFilters() != null && !request.getFilters().isEmpty()) {
            for (Map.Entry<String, Object> entry : request.getFilters().entrySet()) {
                String filterKey = entry.getKey();
                Object filterValue = entry.getValue();

                if (!allowedFilterFields.contains(filterKey)) {
                    throw new IllegalArgumentException("Hệ thống không hỗ trợ lọc theo trường: " + filterKey);
                }

                Path<Object> path = root.get(filterKey);
                if (filterValue instanceof Collection<?> collection) {
                    predicates.add(path.in(collection));
                } else {
                    predicates.add(cb.equal(path, filterValue));
                }
            }
        }

        // Mặc định luôn thêm điều kiện hệ thống
        predicates.add(cb.equal(root.get("isDeleted"), false));

        // --- 3. XỬ LÝ CURSOR ---
        if (request.getCursor() != null) {
            CursorData cursorData = cursorCodec.decodeCursorV2(request.getCursor());
            if (cursorData != null) {
                Predicate cursorPredicate = buildCursorCriteria(cb, root, sortBy, cursorData, isDesc, isFetchingNext);
                predicates.add(cursorPredicate);
            }
        }

        cq.where(cb.and(predicates.toArray(new Predicate[0])));

        // --- 4. SẮP XẾP VÀ TRUY VẤN ---
        boolean queryDesc = isFetchingNext ? isDesc : !isDesc;
        if (request.getCursor() == null) {
            queryDesc = isDesc;
        }

        Order primaryOrder = queryDesc ? cb.desc(root.get(sortBy)) : cb.asc(root.get(sortBy));
        Order secondaryOrder = queryDesc ? cb.desc(root.get("id")) : cb.asc(root.get("id"));
        cq.orderBy(primaryOrder, secondaryOrder);

        TypedQuery<T> query = entityManager.createQuery(cq);
        query.setMaxResults(limit + 1); // Lấy dư 1 để check hasMore
        List<T> data = new ArrayList<>(query.getResultList());

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

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private Predicate buildCursorCriteria(CriteriaBuilder cb, Root<?> root, String sortBy, CursorData cursorData,
            boolean isDesc, boolean isFetchingNext) {
        boolean isLessThan = (isDesc && isFetchingNext) || (!isDesc && !isFetchingNext);

        Path sortPath = root.get(sortBy);
        Path idPath = root.get("id");

        // TỰ ĐỘNG ÉP KIỂU: Postgres rất khắt khe. Nếu ID entity là UUID nhưng cursor
        // gửi String, nó sẽ crash.
        Comparable typedSortValue = (Comparable) parseDynamicValue(cursorData.getSortValue(), sortPath.getJavaType());
        Comparable typedIdValue = (Comparable) parseDynamicValue(cursorData.getId(), idPath.getJavaType());

        Predicate sortCompare;
        Predicate idCompare;

        if (isLessThan) {
            sortCompare = cb.lessThan(sortPath, typedSortValue);
            idCompare = cb.and(
                    cb.equal(sortPath, typedSortValue),
                    cb.lessThan(idPath, typedIdValue));
        } else {
            sortCompare = cb.greaterThan(sortPath, typedSortValue);
            idCompare = cb.and(
                    cb.equal(sortPath, typedSortValue),
                    cb.greaterThan(idPath, typedIdValue));
        }

        return cb.or(sortCompare, idCompare);
    }

    // Tiện ích để ép kiểu dữ liệu từ JSON (Cursor) sang kiểu thực tế của Entity
    private Object parseDynamicValue(Object value, Class<?> targetType) {
        if (value == null)
            return null;
        if (targetType.equals(UUID.class) && value instanceof String) {
            return UUID.fromString((String) value);
        }
        if (targetType.equals(Long.class) && value instanceof Number) {
            return ((Number) value).longValue();
        }
        if (targetType.equals(Integer.class) && value instanceof Number) {
            return ((Number) value).intValue();
        }
        return value;
    }
}
