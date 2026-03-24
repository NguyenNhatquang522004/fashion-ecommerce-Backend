package io.github.nguyennhatquang.fashion.Identity.infrastructure.postgres.adapter;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import io.github.nguyennhatquang.fashion.Identity.domain.IRepository.postgres.IRepositoryUserAddress;
import io.github.nguyennhatquang.fashion.Identity.domain.entity.UserAddress;
import io.github.nguyennhatquang.fashion.Identity.infrastructure.postgres.repository.UserAddressJpaRepo;
import io.github.nguyennhatquang.fashion.common.Utils.CursorUtils;
import io.github.nguyennhatquang.fashion.common.request.PanigationRequest;
import io.github.nguyennhatquang.fashion.common.response.PanigationResponse;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class UserAdressRepositoryImpl implements IRepositoryUserAddress {
    private final UserAddressJpaRepo userAddressJpaRepo;

    @Override
    public UserAddress save(UserAddress userAddress) {
        return userAddressJpaRepo.save(userAddress);
    }

    @Override
    public List<UserAddress> saveAll(List<UserAddress> userAddresses) {
        return userAddressJpaRepo.saveAll(userAddresses);
    }

    @Override
    @Transactional
    public UserAddress update(UserAddress userAddress) {
        return userAddressJpaRepo.save(userAddress);
    }

    @Override
    public List<UserAddress> updateAll(List<UserAddress> userAddresses) {
        return userAddressJpaRepo.saveAll(userAddresses);
    }

    @Override
    public boolean delete(UserAddress userAddress) {
        userAddressJpaRepo.delete(userAddress);
        return true;
    }

    @Override
    public boolean deleteAll(List<UserAddress> userAddresses) {
        userAddressJpaRepo.deleteAll(userAddresses);
        return true;
    }

    @Override
    public Optional<UserAddress> findById(UUID id) {
        return userAddressJpaRepo.findById(id);
    }

    @Override
    public List<UserAddress> findByUserId(UUID userId) {
        return userAddressJpaRepo.findByUserProfile_Id(userId);
    }

    @Override
    // Bạn nên sửa interface IRepositoryUserAddress để thêm tham số UUID userId
    public PanigationResponse<UserAddress> getAddressesCursor(UUID userId, PanigationRequest request) {
        int limit = request.getLimit() != null ? request.getLimit() : 10;
        PageRequest pageRequest = PageRequest.of(0, limit + 1);

        LocalDateTime cursorTime = null;
        UUID cursorId = null;

        if (request.getCursor() != null) {
            Object[] decoded = CursorUtils.decodeCursor(request.getCursor());
            if (decoded != null) {
                cursorTime = (LocalDateTime) decoded[0];
                cursorId = (UUID) decoded[1];
            }
        }

        List<UserAddress> data;
        boolean isFetchingNext = request.getHasNext() != null && request.getHasNext();

        // Xử lý truy vấn (ĐÃ TRUYỀN THÊM userId)
        if (isFetchingNext || request.getCursor() == null) {
            data = userAddressJpaRepo.findNextPageDesc(userId, cursorTime, cursorId, pageRequest);
        } else {
            data = userAddressJpaRepo.findPreviousPageDesc(userId, cursorTime, cursorId, pageRequest);
            Collections.reverse(data);
        }

        boolean hasMore = false;
        if (data.size() > limit) {
            hasMore = true;
            if (isFetchingNext || request.getCursor() == null) {
                data.remove(data.size() - 1);
            } else {
                data.remove(0);
            }
        }

        String nextCursor = null;
        if (!data.isEmpty()) {
            UserAddress lastItem = data.get(data.size() - 1);
            nextCursor = CursorUtils.encodeCursor(lastItem.getCreatedAt(), lastItem.getId());
        }

        boolean responseHasNext;
        boolean responseHasPrevious;

        if (request.getCursor() == null) {
            responseHasPrevious = false;
            responseHasNext = hasMore;
        } else if (isFetchingNext) {
            responseHasPrevious = true;
            responseHasNext = hasMore;
        } else {
            responseHasNext = true;
            responseHasPrevious = hasMore;
        }

        // --- BEST PRACTICE CẢNH BÁO ---
        // Tại tầng Service gọi hàm này, bạn BẮT BUỘC phải map List<UserAddress>
        // thành List<UserAddressResponse> trước khi trả về Controller nhé.
        return PanigationResponse.<UserAddress>builder()
                .cursor(nextCursor)
                .limit(limit)
                .sort("DESC")
                .hasNext(responseHasNext)
                .hasPrevious(responseHasPrevious)
                .data(data)
                .build();
    }
}
