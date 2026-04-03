package io.github.nguyennhatquang.fashion.Order.configuration.Momo;

import java.math.BigDecimal;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.nguyennhatquang.fashion.Order.delivery.Dto.Momo.MomoRequest.MoMoIpnRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProcessMoMoIpnUseCase {
    private final MoMoProperties moMoProperties;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public void execute(MoMoIpnRequest ipnRequest) {
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
