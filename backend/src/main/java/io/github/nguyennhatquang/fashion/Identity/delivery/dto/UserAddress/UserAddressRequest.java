package io.github.nguyennhatquang.fashion.Identity.delivery.dto.UserAddress;

import io.github.nguyennhatquang.fashion.common.Enum.AddressTypeEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAddressRequest {

    @NotBlank(message = "Receiver name is required")
    private String receiverName;

    @NotBlank(message = "Receiver phone is required")
    @Pattern(regexp = "^[0-9]{10,11}$", message = "Invalid phone number format")
    private String receiverPhone;

    @NotBlank(message = "Street line is required")
    private String streetLine;

    @NotBlank(message = "Ward code is required")
    @Size(max = 20)
    private String wardCode;

    @NotBlank(message = "Ward name is required")
    private String wardName;

    @NotBlank(message = "District code is required")
    @Size(max = 20)
    private String districtCode;

    @NotBlank(message = "District name is required")
    private String districtName;

    @NotBlank(message = "Province code is required")
    @Size(max = 20)
    private String provinceCode;

    @NotBlank(message = "Province name is required")
    private String provinceName;

    private Double latitude;
    private Double longitude;

    private AddressTypeEnum addressType;

    @NotNull(message = "isDefault flag must be specified")
    private Boolean isDefault;
}