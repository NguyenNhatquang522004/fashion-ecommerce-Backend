package io.github.nguyennhatquang.fashion.common.infrastructure.Function;

import io.github.nguyennhatquang.fashion.common.request.ExactPageRequest;
import io.github.nguyennhatquang.fashion.common.request.ExactPageRequestv2;
import io.github.nguyennhatquang.fashion.common.response.ExactPageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MongoExactPagePaginationService {
    private final MongoTemplate mongoTemplate;

    public <T> ExactPageResponse<T> execute(
            ExactPageRequestv2 request,
            Class<T> entityClass,
            String collectionName,
            Set<String> allowedSortFields,
            Set<String> allowedFilterFields) {

        // 1. CHỐT SNAPSHOT TIME
        Instant currentSnapshot = request.getSnapshotTime() != null
                ? request.getSnapshotTime().toInstant(ZoneOffset.UTC)
                : Instant.now();

        // 2. VALIDATE BẢO MẬT & SETUP SORT
        String sortBy = request.getSortBy();
        if (!allowedSortFields.contains(sortBy)) {
            throw new IllegalArgumentException("Hệ thống không hỗ trợ sắp xếp theo trường: " + sortBy);
        }
        Sort.Direction direction = "ASC".equalsIgnoreCase(request.getSortDirection()) ? Sort.Direction.ASC
                : Sort.Direction.DESC;

        int limit = request.getLimit() > 0 ? request.getLimit() : 10;
        int skip = Math.max(request.getPage() - 1, 0) * limit;

        // 3. BUILD QUERY ĐỘNG
        Query baseQuery = new Query();

        // Điều kiện kiên quyết 1: Snapshot Time (Áp dụng cho trường createdAt)
        baseQuery.addCriteria(Criteria.where("createdAt").lte(currentSnapshot));

        // Điều kiện kiên quyết 2: Không lấy dữ liệu đã xóa mềm
        baseQuery.addCriteria(Criteria.where("isDeleted").is(false));

        // Tích hợp Dynamic Filters từ Client
        if (request.getFilters() != null && !request.getFilters().isEmpty()) {
            for (Map.Entry<String, Object> entry : request.getFilters().entrySet()) {
                if (!allowedFilterFields.contains(entry.getKey())) {
                    throw new IllegalArgumentException("Không hỗ trợ lọc theo trường: " + entry.getKey());
                }
                if (entry.getValue() instanceof Collection<?> collection) {
                    baseQuery.addCriteria(Criteria.where(entry.getKey()).in(collection));
                } else {
                    baseQuery.addCriteria(Criteria.where(entry.getKey()).is(entry.getValue()));
                }
            }
        }

        // 4. COUNT TOTAL ELEMENTS (Dùng baseQuery chưa có Sort/Skip/Limit)
        long totalElements = mongoTemplate.count(baseQuery, entityClass, collectionName);
        int totalPages = (int) Math.ceil((double) totalElements / limit);
        List<T> responseData = new ArrayList<>();

        if (totalElements > 0) {
            // 5. DEFERRED JOIN - BƯỚC A: LẤY ID SIÊU TỐC
            // Copy query để không ảnh hưởng query gốc, thêm Sort, Skip, Limit và CHỈ LẤY ID
            Query idQuery = Query.of(baseQuery);
            idQuery.with(Sort.by(direction, sortBy).and(Sort.by(direction, "id"))); // Sort ghép id để chống trùng lặp
                                                                                    // thứ tự
            idQuery.skip(skip).limit(limit);
            idQuery.fields().include("id"); // Covered Query: Chỉ kéo mỗi field ID về RAM

            List<T> idOnlyEntities = mongoTemplate.find(idQuery, entityClass, collectionName);
            List<String> ids = idOnlyEntities.stream().map(this::extractId).toList();

            if (!ids.isEmpty()) {
                // 6. DEFERRED JOIN - BƯỚC B: LẤY FULL DATA
                Query fetchQuery = new Query(Criteria.where("id").in(ids));
                List<T> unorderedData = mongoTemplate.find(fetchQuery, entityClass, collectionName);

                // 7. SẮP XẾP LẠI IN-MEMORY (BEST PRACTICE)
                // Toán tử $in của MongoDB KHÔNG đảm bảo trả về đúng thứ tự của list IDs.
                // Ta phải map lại in-memory để đảm bảo thứ tự sort ban đầu.
                Map<String, T> dataMap = unorderedData.stream()
                        .collect(Collectors.toMap(this::extractId, entity -> entity));

                for (String id : ids) {
                    if (dataMap.containsKey(id)) {
                        responseData.add(dataMap.get(id));
                    }
                }
            }
        }

        LocalDateTime responseSnapshot = LocalDateTime.ofInstant(currentSnapshot, ZoneOffset.UTC);

        // 8. TRẢ VỀ RESPONSE
        return ExactPageResponse.<T>builder()
                .currentPage(request.getPage())
                .totalPages(totalPages)
                .totalElements(totalElements)
                .snapshotTime(responseSnapshot)
                .data(responseData)
                .build();
    }

    // Tiện ích để trích xuất linh hoạt field "id" từ mọi loại Entity
    private <T> String extractId(T entity) {
        BeanWrapper beanWrapper = PropertyAccessorFactory.forBeanPropertyAccess(entity);
        return String.valueOf(beanWrapper.getPropertyValue("id"));
    }
}
