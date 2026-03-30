package io.github.nguyennhatquang.fashion.Inventory.delivery.Mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import io.github.nguyennhatquang.fashion.Inventory.delivery.Dto.StockReservation.StockReservationRequest.StockReservationCreateRequest;
import io.github.nguyennhatquang.fashion.Inventory.delivery.Dto.StockReservation.StockReservationRequest.StockReservationCreateRequestv2;
import io.github.nguyennhatquang.fashion.Inventory.delivery.Dto.StockReservation.StockReservationRequest.StockReservationUpdateRequest;
import io.github.nguyennhatquang.fashion.Inventory.delivery.Dto.StockReservation.StockReservationResponse;
import io.github.nguyennhatquang.fashion.Inventory.domain.entity.StockReservation;

import java.util.List;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface StockReservationMapper {

    // 1. Map từ CreateRequest sang Entity
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "warehouse", ignore = true)   // Warehouse được load trong Service
    @Mapping(target = "status", ignore = true)      // Mặc định RESERVED khi tạo mới
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    StockReservation toEntity(StockReservationCreateRequest request);

    // 1. Map từ CreateRequest sang Entity
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "warehouse", ignore = true)   // Warehouse được load trong Service
    @Mapping(target = "status", ignore = true)      // Mặc định RESERVED khi tạo mới
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    StockReservation toEntity(StockReservationCreateRequestv2 request);
    // 2. Map từ UpdateRequest để cập nhật Entity có sẵn
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "orderId", ignore = true)     // Order ID không được đổi
    @Mapping(target = "skuCode", ignore = true)     // SKU không được đổi
    @Mapping(target = "warehouse", ignore = true)
    @Mapping(target = "quantity", ignore = true)    // Số lượng không được đổi
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    void updateEntityFromRequest(StockReservationUpdateRequest request, @MappingTarget StockReservation reservation);

    // 3. Map từ Entity ra Response
    @Mapping(target = "warehouseId", source = "warehouse.id")
    StockReservationResponse toResponse(StockReservation reservation);

    @Mapping(target = "warehouseId", source = "warehouse.id")
    List<StockReservationResponse> toResponseList(List<StockReservation> reservations);
}
