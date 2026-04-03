package io.github.nguyennhatquang.fashion.Order.delivery.Dto.Momo;

import lombok.experimental.UtilityClass;

@UtilityClass
public class MomoRequest {
        // DTO gửi lên MoMo để tạo đơn
        public record MoMoCreateRequest(
                        String partnerCode, String partnerName, String storeId, String requestId,
                        Long amount, String orderId, String orderInfo, String redirectUrl,
                        String ipnUrl, String requestType, String extraData, String lang, String signature) {
        }

        // DTO hứng kết quả MoMo trả về sau khi gọi API tạo đơn
        public record MoMoCreateResponse(
                        String partnerCode, String requestId, String orderId, Long amount,
                        Long responseTime, String message, Integer resultCode, String payUrl,
                        String shortLink, String signature,
                        String qrCodeUrl) {
        }

        // DTO hứng IPN Webhook từ MoMo bắn về
        public record MoMoIpnRequest(
                        String partnerCode, String orderId, String requestId, Long amount,
                        String orderInfo, String orderType, Long transId, Integer resultCode,
                        String message, String payType, Long responseTime, String extraData, String signature) {
        }
}
