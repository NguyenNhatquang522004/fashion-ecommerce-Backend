package io.github.nguyennhatquang.fashion.Order.delivery.Mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import io.github.nguyennhatquang.fashion.Order.delivery.Dto.PaymentTransaction.PaymentTransactionRequest.PaymentTransactionCreateRequest;
import io.github.nguyennhatquang.fashion.Order.delivery.Dto.PaymentTransaction.PaymentTransactionRequest.PaymentTransactionUpdateRequest;
import io.github.nguyennhatquang.fashion.Order.delivery.Dto.PaymentTransaction.PaymentTransactionResponse;
import io.github.nguyennhatquang.fashion.Order.domain.entity.PaymentTransaction;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PaymentTransactionMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isDeleted", constant = "false")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    PaymentTransaction toEntity(PaymentTransactionCreateRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    void updateEntityFromRequest(PaymentTransactionUpdateRequest request, @MappingTarget PaymentTransaction entity);

    PaymentTransactionResponse toResponse(PaymentTransaction entity);

    List<PaymentTransactionResponse> toResponseList(List<PaymentTransaction> entities);
}
