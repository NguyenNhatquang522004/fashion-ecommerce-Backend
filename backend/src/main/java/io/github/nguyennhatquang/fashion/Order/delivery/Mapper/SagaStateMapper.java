package io.github.nguyennhatquang.fashion.Order.delivery.Mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import io.github.nguyennhatquang.fashion.Order.delivery.Dto.SagaState.SagaStateRequest.SagaStateCreateRequest;
import io.github.nguyennhatquang.fashion.Order.delivery.Dto.SagaState.SagaStateRequest.SagaStateUpdateRequest;
import io.github.nguyennhatquang.fashion.Order.delivery.Dto.SagaState.SagaStateResponse;
import io.github.nguyennhatquang.fashion.Order.domain.entity.SagaState;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface SagaStateMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isDeleted", constant = "false")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    SagaState toEntity(SagaStateCreateRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    void updateEntityFromRequest(SagaStateUpdateRequest request, @MappingTarget SagaState entity);

    SagaStateResponse toResponse(SagaState entity);

    List<SagaStateResponse> toResponseList(List<SagaState> entities);
}
