package io.github.nguyennhatquang.fashion.Inventory.usecase.AdapterUseCase;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import io.github.nguyennhatquang.fashion.Inventory.delivery.Dto.FlashSaleItem.FlashSaleItemRequest.FlashSaleItemCreateRequest;
import io.github.nguyennhatquang.fashion.Inventory.delivery.Dto.FlashSaleItem.FlashSaleItemRequest.FlashSaleItemUpdateRequest;
import io.github.nguyennhatquang.fashion.Inventory.delivery.Mapper.FlashSaleItemMapper;
import io.github.nguyennhatquang.fashion.Inventory.domain.IRepository.postgres.IFlashSaleItemRepository;
import io.github.nguyennhatquang.fashion.Inventory.domain.entity.FlashSaleItem;
import io.github.nguyennhatquang.fashion.Inventory.usecase.IUseCase.IAdminFlashSaleItemUseCase;
import io.github.nguyennhatquang.fashion.common.response.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminFlashSaleItemUseCase implements IAdminFlashSaleItemUseCase {
    private final IFlashSaleItemRepository flashSaleItemRepository;
    private final FlashSaleItemMapper flashSaleItemMapper;

    @Override
    public Result<FlashSaleItem, Exception> createFlashSaleItem(FlashSaleItemCreateRequest request) {
        try {
            FlashSaleItem item = flashSaleItemMapper.toEntity(request);
            flashSaleItemRepository.save(item);
            return Result.success(item);
        } catch (Exception e) {
            return Result.error(e);
        }
    }

    @Override
    public Result<FlashSaleItem, Exception> updateFlashSaleItem(FlashSaleItemUpdateRequest request, UUID id) {
        try {
            FlashSaleItem item = flashSaleItemRepository.findById(id).orElse(null);
            if (item == null) {
                return Result.error(new Exception("FlashSaleItem not found"));
            }
            flashSaleItemMapper.updateEntityFromRequest(request, item);
            flashSaleItemRepository.save(item);
            return Result.success(item);
        } catch (Exception e) {
            return Result.error(e);
        }
    }

    @Override
    public Result<Void, Exception> deleteFlashSaleItem(UUID id) {
        try {
            flashSaleItemRepository.deleteById(id);
            return Result.success(null);
        } catch (Exception e) {
            return Result.error(e);
        }
    }

    @Override
    public Result<FlashSaleItem, Exception> getFlashSaleItemById(UUID id) {
        try {
            FlashSaleItem item = flashSaleItemRepository.findById(id).orElse(null);
            if (item == null) {
                return Result.error(new Exception("FlashSaleItem not found"));
            }
            return Result.success(item);
        } catch (Exception e) {
            return Result.error(e);
        }
    }

    @Override
    public Result<List<FlashSaleItem>, Exception> getAllFlashSaleItems() {
        try {
            List<FlashSaleItem> items = flashSaleItemRepository.findAll();
            return Result.success(items);
        } catch (Exception e) {
            return Result.error(e);
        }
    }
}
