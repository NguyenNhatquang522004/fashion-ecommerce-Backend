package io.github.nguyennhatquang.fashion.Order.delivery.Mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import io.github.nguyennhatquang.fashion.Order.delivery.Dto.Voucher.VoucherRequest.VoucherCreateRequest;
import io.github.nguyennhatquang.fashion.Order.delivery.Dto.Voucher.VoucherRequest.VoucherUpdateRequest;
import io.github.nguyennhatquang.fashion.Order.delivery.Dto.Voucher.VoucherResponse;
import io.github.nguyennhatquang.fashion.Order.domain.entity.Voucher;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface VoucherMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isDeleted", constant = "false")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    Voucher toEntity(VoucherCreateRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    void updateEntityFromRequest(VoucherUpdateRequest request, @MappingTarget Voucher entity);

    VoucherResponse toResponse(Voucher entity);

    List<VoucherResponse> toResponseList(List<Voucher> entities);
}
