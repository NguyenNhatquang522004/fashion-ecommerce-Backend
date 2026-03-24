package io.github.nguyennhatquang.fashion.Identity.delivery.dto.UserAddress;

import java.time.LocalDateTime;
import java.util.UUID;

import io.github.nguyennhatquang.fashion.common.Enum.AddressTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAddressResponse {
    private UUID id;
    private String receiverName;
    private String receiverPhone;
    private String streetLine;
    private String wardCode;
    private String wardName;
    private String districtCode;
    private String districtName;
    private String provinceCode;
    private String provinceName;
    private Double latitude;
    private Double longitude;
    private AddressTypeEnum addressType;
    private Boolean isDefault;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
