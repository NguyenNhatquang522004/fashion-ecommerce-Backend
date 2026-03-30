package io.github.nguyennhatquang.fashion.Inventory.usecase.IUseCase;

import java.util.List;
import java.util.UUID;

import io.github.nguyennhatquang.fashion.Inventory.delivery.Dto.StockReservation.StockReservationRequest.StockReservationCreateRequest;
import io.github.nguyennhatquang.fashion.Inventory.delivery.Dto.StockReservation.StockReservationRequest.StockReservationUpdateRequest;
import io.github.nguyennhatquang.fashion.Inventory.domain.entity.StockReservation;
import io.github.nguyennhatquang.fashion.common.request.ExactPageRequest;
import io.github.nguyennhatquang.fashion.common.response.ExactPageResponse;
import io.github.nguyennhatquang.fashion.common.response.Result;

public interface IAdminStockReservationUseCase {
    Result<StockReservation, Exception> createStockReservation(StockReservationCreateRequest request);

    Result<StockReservation, Exception> updateStockReservation(StockReservationUpdateRequest request, UUID id);

    Result<Void, Exception> deleteStockReservation(UUID id);

    Result<StockReservation, Exception> getStockReservationById(UUID id);

    Result<ExactPageResponse<StockReservation>, Exception> getAllStockReservations(ExactPageRequest request);
}
