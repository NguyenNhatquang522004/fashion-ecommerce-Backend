package io.github.nguyennhatquang.fashion.Order.delivery.Dto.OrderItem;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

@UtilityClass
public class OrderItemRequest {
    public record OrderItemCreateRequest(
            @NotBlank(message = "Product ID không được để trống") String productId,

            @NotNull(message = "Order ID không được để trống") UUID orderId,

            @NotBlank(message = "Mã SKU không được để trống") String skuCode,

            @NotBlank(message = "Tên sản phẩm không được để trống") String productName,

           @NotNull(message = "Thuộc tính biến thể không được để trống")
            @Valid // Kích hoạt validate trên VariantAttributesDto
            @JsonProperty("variant_attributes") // Mapping field chuẩn Snake_case từ Request JSON
            VariantAttributesDto variantAttributes,

            @NotNull(message = "Đơn giá không được để trống") BigDecimal unitPrice,

            @NotNull(message = "Số lượng không được để trống") Integer quantity) {
    }

    public record OrderItemUpdateRequest(
            @NotBlank(message = "Product ID không được để trống") String productId,

            @NotNull(message = "Order ID không được để trống") UUID orderId,

            @NotBlank(message = "Mã SKU không được để trống") String skuCode,

            @NotBlank(message = "Tên sản phẩm không được để trống") String productName,

            @NotNull(message = "Thuộc tính biến thể không được để trống") @Valid // Kích hoạt validate trên
                                                                                 // VariantAttributesDto
            @JsonProperty("variant_attributes") // Mapping field chuẩn Snake_case từ Request JSON
            VariantAttributesDto variantAttributes,

            @NotNull(message = "Đơn giá không được để trống") BigDecimal unitPrice,

            @NotNull(message = "Số lượng không được để trống") Integer quantity) {
    }

    public record PresentationDto(
            @JsonProperty("brand_name") String brandName,

            @JsonProperty("variant_image_url") @NotBlank(message = "URL hình ảnh không được để trống") String variantImageUrl) {
    }

    public record FulfillmentDto(
            @JsonProperty("weight_grams") @NotNull(message = "Khối lượng không được để trống") @Positive(message = "Khối lượng phải lớn hơn 0") Integer weightGrams,

            @NotBlank(message = "Mã vạch không được để trống") String barcode) {
    }

    public record VariantAttributesDto(
            @NotEmpty(message = "Danh sách tùy chọn (Options) không được để trống") Map<String, String> options,

            @NotNull(message = "Thông tin hiển thị (Presentation) không được để trống") @Valid // Kích hoạt validate
                                                                                               // lồng nhau (Cascade
                                                                                               // validation)
            @JsonProperty("presentation") PresentationDto presentation,

            @NotNull(message = "Thông tin vận hành (Fulfillment) không được để trống") @Valid // Kích hoạt validate lồng
                                                                                              // nhau
            @JsonProperty("fulfillment") FulfillmentDto fulfillment) {
    }
}
