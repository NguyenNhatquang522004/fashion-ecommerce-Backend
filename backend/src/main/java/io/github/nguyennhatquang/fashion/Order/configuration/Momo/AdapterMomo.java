package io.github.nguyennhatquang.fashion.Order.configuration.Momo;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import io.github.nguyennhatquang.fashion.Order.delivery.Dto.Momo.MomoRequest.MoMoCreateRequest;
import io.github.nguyennhatquang.fashion.Order.delivery.Dto.Momo.MomoRequest.MoMoCreateResponse;
import io.github.nguyennhatquang.fashion.Order.delivery.Dto.Momo.MomoRequest.MoMoIpnRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdapterMomo implements IMomo {
    private final MoMoProperties moMoProperties;
    private final RestTemplate restTemplate = new RestTemplate(); // Nên cấu hình thành Bean

    public String createPaymentUrl(long amount, String orderCode) {
        String requestId = UUID.randomUUID().toString();
        String orderInfo = "Thanh toan don hang " + orderCode;
        String extraData = ""; // Có thể truyền base64 JSON nếu muốn nhét thêm data
        String requestType = "captureWallet"; // Quét mã QR MoMo

        // 1. Tạo chuỗi ký tự theo ĐÚNG THỨ TỰ TỪ ĐIỂN mà MoMo yêu cầu
        String rawSignature = "accessKey=" + moMoProperties.getAccessKey() +
                "&amount=" + amount +
                "&extraData=" + extraData +
                "&ipnUrl=" + moMoProperties.getIpnUrl() + // MoMo truyền IPN ở đây!
                "&orderId=" + orderCode +
                "&orderInfo=" + orderInfo +
                "&partnerCode=" + moMoProperties.getPartnerCode() +
                "&redirectUrl=" + moMoProperties.getRedirectUrl() +
                "&requestId=" + requestId +
                "&requestType=" + requestType;

        // 2. Ký chữ ký
        String signature = MoMoUtil.hmacSHA256(moMoProperties.getSecretKey(), rawSignature);

        // 3. Build Body Request
        MoMoCreateRequest requestBody = new MoMoCreateRequest(
                moMoProperties.getPartnerCode(), "Fashion Shop", "Fashion Store", requestId,
                amount, orderCode, orderInfo, moMoProperties.getRedirectUrl(),
                moMoProperties.getIpnUrl(), requestType, extraData, "vi", signature);

        // 4. Gọi API sang MoMo để lấy link thanh toán
        try {
            MoMoCreateResponse response = restTemplate.postForObject(
                    moMoProperties.getEndpoint(), requestBody, MoMoCreateResponse.class);

            if (response != null && response.resultCode() == 0) {
                return response.qrCodeUrl(); // Trả cái link này cho Frontend redirect
            } else {
                throw new RuntimeException(
                        "MoMo creation failed: " + (response != null ? response.message() : "Unknown error"));
            }
        } catch (Exception e) {
            log.error("Error calling MoMo API", e);
            throw new RuntimeException("Cannot create MoMo payment", e);
        }
    }

    @Transactional
    public void processIpn(MoMoIpnRequest ipnRequest) {
        // 1. Dựng lại chuỗi để kiểm tra chữ ký (Format này MoMo quy định nghiêm ngặt)
        String rawSignature = "accessKey=" + moMoProperties.getAccessKey() +
                "&amount=" + ipnRequest.amount() +
                "&extraData=" + ipnRequest.extraData() +
                "&message=" + ipnRequest.message() +
                "&orderId=" + ipnRequest.orderId() +
                "&orderInfo=" + ipnRequest.orderInfo() +
                "&partnerCode=" + ipnRequest.partnerCode() +
                "&payType=" + ipnRequest.payType() +
                "&requestId=" + ipnRequest.requestId() +
                "&responseTime=" + ipnRequest.responseTime() +
                "&resultCode=" + ipnRequest.resultCode() +
                "&transId=" + ipnRequest.transId();

        String expectedSignature = MoMoUtil.hmacSHA256(moMoProperties.getSecretKey(), rawSignature);

        if (!expectedSignature.equals(ipnRequest.signature())) {
            log.error("MoMo IPN: Invalid Signature for Order: {}", ipnRequest.orderId());
            throw new IllegalArgumentException("Invalid MoMo signature");
        }

        // 2. Lưu Audit Log vào bảng PaymentTransaction
        // (Bạn tạo object giống như bài VNPay, lưu kèm chuỗi JSON gốc vào cột raw_data)

        // 3. Xử lý logic và Phát Event (Tái sử dụng chung Event với VNPay)
        // MoMo resultCode == 0 nghĩa là THÀNH CÔNG
        String status = (ipnRequest.resultCode() == 0) ? "SUCCESS" : "FAILED";

        // eventPublisher.publishEvent(new PaymentStatusChangedEvent(
        // ipnRequest.orderId(),
        // BigDecimal.valueOf(ipnRequest.amount()),
        // status,
        // String.valueOf(ipnRequest.transId()), // Mã giao dịch của MoMo
        // null // Truyền map JSON vào nếu cần
        // ));

        log.info("MoMo IPN processed successfully for Order: {}", ipnRequest.orderId());
    }
}
