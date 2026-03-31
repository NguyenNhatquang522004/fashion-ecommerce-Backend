package io.github.nguyennhatquang.fashion.common.infrastructure.Function;

import io.github.nguyennhatquang.fashion.common.request.ExactPageRequestv2;
import io.github.nguyennhatquang.fashion.common.response.ExactPageResponse;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Tuple;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JpaExactPagePaginationService {
    private final EntityManager entityManager;

    public <T> ExactPageResponse<T> execute(
            ExactPageRequestv2 request,
            Class<T> entityClass,
            Set<String> allowedSortFields,
            Set<String> allowedFilterFields) {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        // 1. CHỐT SNAPSHOT TIME
        Instant currentSnapshot = request.getSnapshotTime() != null
                ? request.getSnapshotTime().toInstant(ZoneOffset.UTC)
                : Instant.now();

        // 2. VALIDATE BẢO MẬT & SETUP SORT
        String sortBy = request.getSortBy();
        if (!allowedSortFields.contains(sortBy)) {
            throw new IllegalArgumentException("Hệ thống không hỗ trợ sắp xếp theo trường: " + sortBy);
        }
        boolean isAsc = "ASC".equalsIgnoreCase(request.getSortDirection());

        int limit = request.getLimit() > 0 ? request.getLimit() : 10;
        int skip = Math.max(request.getPage() - 1, 0) * limit;

        // 3. COUNT TOTAL ELEMENTS
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<T> countRoot = countQuery.from(entityClass);
        List<Predicate> countPredicates = buildPredicates(cb, countRoot, request, allowedFilterFields, currentSnapshot);

        countQuery.select(cb.count(countRoot))
                .where(cb.and(countPredicates.toArray(new Predicate[0])));

        long totalElements = entityManager.createQuery(countQuery).getSingleResult();
        int totalPages = (int) Math.ceil((double) totalElements / limit);
        List<T> responseData = new ArrayList<>();

        if (totalElements > 0) {
            // 4. DEFERRED JOIN - BƯỚC A: LẤY ID SIÊU TỐC (Dùng Tuple để lấy partial data)
            CriteriaQuery<Tuple> idQuery = cb.createQuery(Tuple.class);
            Root<T> idRoot = idQuery.from(entityClass);
            List<Predicate> idPredicates = buildPredicates(cb, idRoot, request, allowedFilterFields, currentSnapshot);

            idQuery.multiselect(idRoot.get("id").alias("id"))
                    .where(cb.and(idPredicates.toArray(new Predicate[0])));

            Order primaryOrder = isAsc ? cb.asc(idRoot.get(sortBy)) : cb.desc(idRoot.get(sortBy));
            Order secondaryOrder = isAsc ? cb.asc(idRoot.get("id")) : cb.desc(idRoot.get("id"));
            idQuery.orderBy(primaryOrder, secondaryOrder);

            List<Tuple> idTuples = entityManager.createQuery(idQuery)
                    .setFirstResult(skip)
                    .setMaxResults(limit)
                    .getResultList();

            // Chuyển ID về dạng String để dễ dàng map in-memory
            List<String> stringIds = idTuples.stream()
                    .map(t -> String.valueOf(t.get("id")))
                    .toList();

            if (!stringIds.isEmpty()) {
                // 5. DEFERRED JOIN - BƯỚC B: LẤY FULL DATA
                CriteriaQuery<T> fetchQuery = cb.createQuery(entityClass);
                Root<T> fetchRoot = fetchQuery.from(entityClass);

                // Ép kiểu ID ngược lại từ String sang kiểu thực tế (UUID, Long, v.v.) của
                // Entity
                Class<?> idJavaType = fetchRoot.get("id").getJavaType();
                List<Object> typedIds = stringIds.stream()
                        .map(idStr -> parseDynamicValue(idStr, idJavaType))
                        .toList();

                fetchQuery.select(fetchRoot)
                        .where(fetchRoot.get("id").in(typedIds));

                List<T> unorderedData = entityManager.createQuery(fetchQuery).getResultList();

                // 6. SẮP XẾP LẠI IN-MEMORY (BEST PRACTICE)
                // Cùng lý do như Mongo, PostgreSQL IN clause KHÔNG đảm bảo trả về đúng thứ tự.
                Map<String, T> dataMap = unorderedData.stream()
                        .collect(Collectors.toMap(this::extractId, entity -> entity));

                for (String id : stringIds) {
                    if (dataMap.containsKey(id)) {
                        responseData.add(dataMap.get(id));
                    }
                }
            }
        }

        LocalDateTime responseSnapshot = LocalDateTime.ofInstant(currentSnapshot, ZoneOffset.UTC);

        // 7. TRẢ VỀ RESPONSE
        return ExactPageResponse.<T>builder()
                .currentPage(request.getPage())
                .totalPages(totalPages)
                .totalElements(totalElements)
                .snapshotTime(responseSnapshot)
                .data(responseData)
                .build();
    }

    // Hàm tiện ích: Build danh sách điều kiện lọc động dùng chung cho cả Count
    // Query và Data Query
    private <T> List<Predicate> buildPredicates(
            CriteriaBuilder cb,
            Root<T> root,
            ExactPageRequestv2 request,
            Set<String> allowedFilterFields,
            Instant currentSnapshot) {

        List<Predicate> predicates = new ArrayList<>();

        // Điều kiện kiên quyết 1: Snapshot Time
        predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), currentSnapshot));

        // Điều kiện kiên quyết 2: Không lấy dữ liệu đã xóa mềm
        predicates.add(cb.equal(root.get("isDeleted"), false));

        // Tích hợp Dynamic Filters từ Client
        if (request.getFilters() != null && !request.getFilters().isEmpty()) {
            for (Map.Entry<String, Object> entry : request.getFilters().entrySet()) {
                String key = entry.getKey();
                if (!allowedFilterFields.contains(key)) {
                    throw new IllegalArgumentException("Không hỗ trợ lọc theo trường: " + key);
                }

                Path<Object> path = root.get(key);
                Object rawValue = entry.getValue();

                if (rawValue instanceof Collection<?> collection) {
                    // Nếu là List -> IN clause
                    CriteriaBuilder.In<Object> inClause = cb.in(path);
                    for (Object item : collection) {
                        inClause.value(parseDynamicValue(item, path.getJavaType()));
                    }
                    predicates.add(inClause);
                } else {
                    // Nếu là giá trị đơn -> Bằng clause
                    Object typedValue = parseDynamicValue(rawValue, path.getJavaType());
                    predicates.add(cb.equal(path, typedValue));
                }
            }
        }
        return predicates;
    }

    // Tiện ích ép kiểu: Tránh lỗi ClassCastException đặc trưng của PostgreSQL
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
        // Xử lý tự động ép kiểu String thành Enum nếu Target field là Enum
        if (targetType.isEnum() && value instanceof String) {
            @SuppressWarnings({ "unchecked", "rawtypes" })
            Enum<?> enumValue = Enum.valueOf((Class<Enum>) targetType, (String) value);
            return enumValue;
        }
        return value;
    }

    // Tiện ích trích xuất linh hoạt field "id"
    private <T> String extractId(T entity) {
        BeanWrapper beanWrapper = PropertyAccessorFactory.forBeanPropertyAccess(entity);
        return String.valueOf(beanWrapper.getPropertyValue("id"));
    }
}
