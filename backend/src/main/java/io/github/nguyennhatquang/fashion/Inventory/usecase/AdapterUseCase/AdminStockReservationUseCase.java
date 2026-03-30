package io.github.nguyennhatquang.fashion.Inventory.usecase.AdapterUseCase;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import io.github.nguyennhatquang.fashion.Inventory.delivery.Dto.StockReservation.StockReservationRequest.StockReservationCreateRequest;
import io.github.nguyennhatquang.fashion.Inventory.delivery.Dto.StockReservation.StockReservationRequest.StockReservationUpdateRequest;
import io.github.nguyennhatquang.fashion.Inventory.delivery.Mapper.StockReservationMapper;
import io.github.nguyennhatquang.fashion.Inventory.domain.IRepository.postgres.IStockReservationRepository;
import io.github.nguyennhatquang.fashion.Inventory.domain.entity.StockReservation;
import io.github.nguyennhatquang.fashion.Inventory.usecase.IUseCase.IAdminStockReservationUseCase;
import io.github.nguyennhatquang.fashion.common.request.ExactPageRequest;
import io.github.nguyennhatquang.fashion.common.response.ExactPageResponse;
import io.github.nguyennhatquang.fashion.common.response.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminStockReservationUseCase implements IAdminStockReservationUseCase {
    private final IStockReservationRepository reservationRepository;
    private final StockReservationMapper reservationMapper;

    @Override
    public Result<StockReservation, Exception> createStockReservation(StockReservationCreateRequest request) {
        try {
            StockReservation reservation = reservationMapper.toEntity(request);
            reservationRepository.save(reservation);
            return Result.success(reservation);
        } catch (Exception e) {
            return Result.error(e);
        }
    }

    @Override
    public Result<StockReservation, Exception> updateStockReservation(StockReservationUpdateRequest request, UUID id) {
        try {
            StockReservation reservation = reservationRepository.findById(id).orElse(null);
            if (reservation == null) {
                return Result.error(new Exception("StockReservation not found"));
            }
            reservationMapper.updateEntityFromRequest(request, reservation);
            reservationRepository.save(reservation);
            return Result.success(reservation);
        } catch (Exception e) {
            return Result.error(e);
        }
    }

    @Override
    public Result<Void, Exception> deleteStockReservation(UUID id) {
        try {
            reservationRepository.deleteById(id);
            return Result.success(null);
        } catch (Exception e) {
            return Result.error(e);
        }
    }

    @Override
    public Result<StockReservation, Exception> getStockReservationById(UUID id) {
        try {
            StockReservation reservation = reservationRepository.findById(id).orElse(null);
            if (reservation == null) {
                return Result.error(new Exception("StockReservation not found"));
            }
            return Result.success(reservation);
        } catch (Exception e) {
            return Result.error(e);
        }
    }

    @Override
    public Result<ExactPageResponse<StockReservation>, Exception> getAllStockReservations(ExactPageRequest request) {
        try {
            ExactPageResponse<StockReservation> reservations = reservationRepository.getStockReservationExactPage(request);
            return Result.success(reservations);
        } catch (Exception e) {
            return Result.error(e);
        }
    }
}
