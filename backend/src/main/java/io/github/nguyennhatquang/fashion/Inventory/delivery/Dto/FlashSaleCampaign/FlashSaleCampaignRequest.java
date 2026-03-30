package io.github.nguyennhatquang.fashion.Inventory.delivery.Dto.FlashSaleCampaign;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.experimental.UtilityClass;

import java.time.OffsetDateTime;

@UtilityClass
public class FlashSaleCampaignRequest {

    public record FlashSaleCampaignCreateRequest(
            @NotBlank(message = "Tên chiến dịch không được để trống")
            String name,

            @NotNull(message = "Thời gian bắt đầu là bắt buộc")
            OffsetDateTime startTime,

            @NotNull(message = "Thời gian kết thúc là bắt buộc")
            OffsetDateTime endTime
    ) {}

    public record FlashSaleCampaignUpdateRequest(
            @NotBlank(message = "Tên chiến dịch không được để trống")
            String name,

            @NotNull(message = "Thời gian bắt đầu là bắt buộc")
            OffsetDateTime startTime,

            @NotNull(message = "Thời gian kết thúc là bắt buộc")
            OffsetDateTime endTime
    ) {}
}
