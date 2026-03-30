package io.github.nguyennhatquang.fashion.Inventory.delivery.Mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import io.github.nguyennhatquang.fashion.Inventory.delivery.Dto.OutboxEventInventory.OutboxEventInventoryRequest.OutboxEventInventoryCreateRequest;
import io.github.nguyennhatquang.fashion.Inventory.delivery.Dto.OutboxEventInventory.OutboxEventInventoryRequest.OutboxEventInventoryUpdateRequest;
import io.github.nguyennhatquang.fashion.Inventory.delivery.Dto.OutboxEventInventory.OutboxEventInventoryResponse;
import io.github.nguyennhatquang.fashion.Inventory.domain.entity.OutboxEventInventory;

import java.util.List;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface OutboxEventInventoryMapper {

    // 1. Map từ CreateRequest sang Entity
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)       // Mặc định PENDING khi tạo mới
    @Mapping(target = "processedAt", ignore = true)  // Set bởi poller sau khi publish Kafka
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    OutboxEventInventory toEntity(OutboxEventInventoryCreateRequest request);

    // 2. Map từ UpdateRequest để cập nhật trạng thái
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "aggregateType", ignore = true)
    @Mapping(target = "aggregateId", ignore = true)
    @Mapping(target = "type", ignore = true)
    @Mapping(target = "payload", ignore = true)
    @Mapping(target = "processedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    void updateEntityFromRequest(OutboxEventInventoryUpdateRequest request, @MappingTarget OutboxEventInventory outboxEvent);

    // 3. Map từ Entity ra Response
    OutboxEventInventoryResponse toResponse(OutboxEventInventory outboxEvent);

    List<OutboxEventInventoryResponse> toResponseList(List<OutboxEventInventory> outboxEvents);
}
