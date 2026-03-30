package io.github.nguyennhatquang.fashion.Inventory.delivery.Dto.Warehouse;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.experimental.UtilityClass;

@UtilityClass
public class WarehouseRequest {

    public record WarehouseCreateRequest(
            @NotBlank(message = "Mã kho không được để trống")
            String code,

            @NotBlank(message = "Tên kho không được để trống")
            String name,

            @NotBlank(message = "Địa chỉ kho không được để trống")
            String address,

            @NotNull(message = "Trạng thái is_active là bắt buộc")
            Boolean isActive
    ) {}

    public record WarehouseUpdateRequest(
            @NotBlank(message = "Tên kho không được để trống")
            String name,

            @NotBlank(message = "Địa chỉ kho không được để trống")
            String address,

            @NotNull(message = "Trạng thái is_active là bắt buộc")
            Boolean isActive
    ) {}
}
