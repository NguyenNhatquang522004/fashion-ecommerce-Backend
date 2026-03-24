package io.github.nguyennhatquang.fashion.Identity.infrastructure.postgres.adapter;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;


import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import io.github.nguyennhatquang.fashion.Identity.domain.IRepository.postgres.IRepositoryUserProfile;
import io.github.nguyennhatquang.fashion.Identity.domain.entity.UserProfile;
import io.github.nguyennhatquang.fashion.Identity.infrastructure.postgres.repository.UserProfileJpaRepo;
import io.github.nguyennhatquang.fashion.common.Utils.CursorUtils;
import io.github.nguyennhatquang.fashion.common.request.ExactPageRequest;
import io.github.nguyennhatquang.fashion.common.request.PanigationRequest;
import io.github.nguyennhatquang.fashion.common.response.ExactPageResponse;
import io.github.nguyennhatquang.fashion.common.response.PanigationResponse;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class UserProfileRepositoryImpl implements IRepositoryUserProfile {
    private final UserProfileJpaRepo userProfileJpaRepo;

    @Override
    public UserProfile save(UserProfile userProfile) {
        return userProfileJpaRepo.save(userProfile);
    }

    @Override
    public List<UserProfile> saveAll(List<UserProfile> userProfiles) {
        return userProfileJpaRepo.saveAll(userProfiles);
    }

    @Override
    public UserProfile update(UserProfile userProfile) {
        return userProfileJpaRepo.save(userProfile);
    }

    @Override
    public List<UserProfile> updateAll(List<UserProfile> userProfiles) {
        return userProfileJpaRepo.saveAll(userProfiles);
    }

    @Override
    public void delete(UserProfile userProfile) {
        userProfileJpaRepo.delete(userProfile);
    }

    @Override
    public void deleteAll(List<UserProfile> userProfiles) {
        userProfileJpaRepo.deleteAll(userProfiles);
    }

    @Override
    public UserProfile findById(UUID id) {
        return userProfileJpaRepo.findById(id).orElse(null);
    }

    @Override
    public UserProfile findByUserId(UUID userId) {
        return userProfileJpaRepo.findByUserId(userId).orElse(null);
    }

    @Override
    public UserProfile findbykeycloakId(String keycloakId) {
        return userProfileJpaRepo.findByKeycloakId(keycloakId).orElse(null);
    }

    @Override
    public UserProfile findbyEmail(String email) {
        return userProfileJpaRepo.findByEmail(email).orElse(null);
    }

    @Override
    public void deleteByEmail(String email) {
        UserProfile userProfile = userProfileJpaRepo.findByEmail(email).orElse(null);
        if (userProfile != null) {
            userProfileJpaRepo.delete(userProfile);
        }
    }

    @Override
    public PanigationResponse<UserProfile> getProfilesCursor(PanigationRequest request) {
        int limit = request.getLimit() != null ? request.getLimit() : 10;
        // Fetch dư 1 record để check hasNext
        PageRequest pageRequest = PageRequest.of(0, limit + 1);

        LocalDateTime cursorTime = null;
        UUID cursorId = null;

        // Decode Cursor nếu có
        if (request.getCursor() != null) {
            Object[] decoded = CursorUtils.decodeCursor(request.getCursor());
            if (decoded != null) {
                cursorTime = (LocalDateTime) decoded[0];
                cursorId = (UUID) decoded[1];
            }
        }

        List<UserProfile> data;
        boolean hasMore = false;
        boolean isFetchingNext = request.getHasNext() != null && request.getHasNext();

        // Xử lý truy vấn
        if (isFetchingNext || request.getCursor() == null) {
            // Cuộn xuống (Next) hoặc Lần gọi đầu tiên (không có cursor)
            data = userProfileJpaRepo.findNextPageDesc(cursorTime, cursorId, pageRequest);
        } else {
            // Cuộn lên (Previous)
            data = userProfileJpaRepo.findPreviousPageDesc(cursorTime, cursorId, pageRequest);
            // Vì ta query ASC để lấy phần tử phía trên, nên phải đảo ngược mảng lại cho
            // đúng thứ tự DESC
            Collections.reverse(data);
        }

        // Kiểm tra xem có vượt quá limit (nghĩa là còn trang tiếp) không
        if (data.size() > limit) {
            hasMore = true;
            if (isFetchingNext || request.getCursor() == null) {
                data.remove(data.size() - 1); // Bỏ phần tử dư ở cuối
            } else {
                data.remove(0); // Nếu là previous, phần tử dư nằm ở đầu sau khi reverse
            }
        }

        // Tạo Cursor mới dựa trên phần tử cuối cùng của list hiện tại
        String nextCursor = null;
        if (!data.isEmpty()) {
            UserProfile lastItem = data.get(data.size() - 1);
            nextCursor = CursorUtils.encodeCursor(lastItem.getCreatedAt(), lastItem.getId());
        }

        // Tính toán các cờ (Flags)
        boolean responseHasNext;
        boolean responseHasPrevious;

        if (request.getCursor() == null) {
            responseHasPrevious = false;
            responseHasNext = hasMore;
        } else if (isFetchingNext) {
            responseHasPrevious = true; // Chắc chắn có previous vì đã có cursor
            responseHasNext = hasMore;
        } else {
            responseHasNext = true; // Chắc chắn có next vì đang đi lùi lên
            responseHasPrevious = hasMore;
        }

        return PanigationResponse.<UserProfile>builder()
                .cursor(nextCursor)
                .limit(limit)
                .sort("DESC")
                .hasNext(responseHasNext)
                .hasPrevious(responseHasPrevious)
                .data(data)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public ExactPageResponse<UserProfile> getProfilesExactPage(ExactPageRequest request) {
        // 1. Xác định Snapshot Time (Nếu gọi lần đầu, lấy thời gian hiện tại)
        LocalDateTime currentSnapshot = request.getSnapshotTime() != null
                ? request.getSnapshotTime()
                : LocalDateTime.now();

        int page = Math.max(request.getPage() - 1, 0); // JPA Pageable bắt đầu từ 0
        int limit = request.getLimit() > 0 ? request.getLimit() : 10;
        PageRequest pageRequest = PageRequest.of(page, limit);

        // 2. Lấy tổng số lượng (Để tính số trang)
        long totalElements = userProfileJpaRepo.countBySnapshot(currentSnapshot);
        int totalPages = (int) Math.ceil((double) totalElements / limit);

        // 3. Áp dụng Deferred Join
        List<UserProfile> responseData = Collections.emptyList();

        if (totalElements > 0) {
            // Bước A: Tìm tập IDs cho trang hiện tại
            List<UUID> ids = userProfileJpaRepo.findIdsBySnapshot(currentSnapshot, pageRequest);

            // Bước B: Nếu có IDs, query lấy full data
            if (!ids.isEmpty()) {
                responseData = userProfileJpaRepo.fetchFullDataByIds(ids);
            }
        }

        // 4. Trả về kết quả
        return ExactPageResponse.<UserProfile>builder()
                .currentPage(request.getPage()) // Trả về page dạng 1-based cho client dễ dùng
                .totalPages(totalPages)
                .totalElements(totalElements)
                .snapshotTime(currentSnapshot) // Bắt buộc trả về để Client dùng cho lần gọi sau
                .data(responseData)
                .build();
    }
}
