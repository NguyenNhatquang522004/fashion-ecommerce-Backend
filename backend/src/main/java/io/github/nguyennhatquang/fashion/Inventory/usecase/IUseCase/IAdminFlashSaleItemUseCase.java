package io.github.nguyennhatquang.fashion.Inventory.usecase.IUseCase;

import java.util.List;
import java.util.UUID;

import io.github.nguyennhatquang.fashion.Inventory.delivery.Dto.FlashSaleItem.FlashSaleItemRequest.FlashSaleItemCreateRequest;
import io.github.nguyennhatquang.fashion.Inventory.delivery.Dto.FlashSaleItem.FlashSaleItemRequest.FlashSaleItemUpdateRequest;
import io.github.nguyennhatquang.fashion.Inventory.domain.entity.FlashSaleItem;
import io.github.nguyennhatquang.fashion.common.response.Result;

public interface IAdminFlashSaleItemUseCase {
    Result<FlashSaleItem, Exception> createFlashSaleItem(FlashSaleItemCreateRequest request);

    Result<FlashSaleItem, Exception> updateFlashSaleItem(FlashSaleItemUpdateRequest request, UUID id);

    Result<Void, Exception> deleteFlashSaleItem(UUID id);

    Result<FlashSaleItem, Exception> getFlashSaleItemById(UUID id);

    Result<List<FlashSaleItem>, Exception> getAllFlashSaleItems();
}
