package io.github.nguyennhatquang.fashion.Order.configuration.Vnpay;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdapterVnPay implements IVnpay {
    private final VnPayProperties vnPayProperties;

    public String generateVnPayUrl(long amount, String orderCode, String ipAddress) {
        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", vnPayProperties.getVersion());
        vnp_Params.put("vnp_Command", vnPayProperties.getCommand());
        vnp_Params.put("vnp_TmnCode", vnPayProperties.getTmnCode());
        vnp_Params.put("vnp_Amount", String.valueOf(amount * 100)); // VNPay yêu cầu x100
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_TxnRef", orderCode);
        vnp_Params.put("vnp_OrderInfo", "Thanh toan don hang: " + orderCode);
        vnp_Params.put("vnp_OrderType", "other");
        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_ReturnUrl", vnPayProperties.getReturnUrl());
        vnp_Params.put("vnp_IpAddr", ipAddress);

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        vnp_Params.put("vnp_CreateDate", formatter.format(cld.getTime()));

        // Timeout sau 15 phút
        cld.add(Calendar.MINUTE, 15);
        vnp_Params.put("vnp_ExpireDate", formatter.format(cld.getTime()));

        // Build query string
        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();

        try {
            Iterator<String> itr = fieldNames.iterator();
            while (itr.hasNext()) {
                String fieldName = itr.next();
                String fieldValue = vnp_Params.get(fieldName);
                if ((fieldValue != null) && (fieldValue.length() > 0)) {
                    hashData.append(fieldName).append('=')
                            .append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                    query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString())).append('=')
                            .append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                    if (itr.hasNext()) {
                        query.append('&');
                        hashData.append('&');
                    }
                }
            }
            String queryUrl = query.toString();
            String vnp_SecureHash = VnPayUtil.hmacSHA512(vnPayProperties.getSecretKey(), hashData.toString());
            queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
            return vnPayProperties.getPayUrl() + "?" + queryUrl;
        } catch (Exception e) {
            throw new RuntimeException("Error while generating VNPay URL", e);
        }
    }

    @Transactional
    public String processVnPayIpn(Map<String, String> requestParams) {
        try {
            String vnp_SecureHash = requestParams.get("vnp_SecureHash");
            requestParams.remove("vnp_SecureHash");
            requestParams.remove("vnp_SecureHashType");

            // 1. Kiểm tra chữ ký (Checksum)
            String signValue = VnPayUtil.hashAllFields(requestParams, vnPayProperties.getSecretKey());
            if (!signValue.equals(vnp_SecureHash)) {
                log.error("VNPay IPN: Invalid Signature");
                return "{\"RspCode\":\"97\",\"Message\":\"Invalid Checksum\"}";
            }

            String orderCode = requestParams.get("vnp_TxnRef");
            String responseCode = requestParams.get("vnp_ResponseCode");

            // 2. TẠI ĐÂY: Query Payment/Order từ DB để check.
            // - Kiểm tra Order có tồn tại không? (RspCode 01)
            // - Kiểm tra Amount có khớp không? (RspCode 04)
            // - Kiểm tra Order đã được thanh toán trước đó chưa? (RspCode 02 - Idempotency)
            // Giả mã:
            // if (orderNotFound) return "{\"RspCode\":\"01\",\"Message\":\"Order not
            // found\"}";
            // if (orderAlreadyPaid) return "{\"RspCode\":\"02\",\"Message\":\"Order already
            // confirmed\"}";
            // if (amountNotMatch) return "{\"RspCode\":\"04\",\"Message\":\"Invalid
            // amount\"}";

            // 3. Xử lý trạng thái
            if ("00".equals(responseCode)) {
                log.info("VNPay IPN: Payment Success for Order: {}", orderCode);

                // Publish Event cho module Order và Inventory xử lý tiếp theo chuẩn Modulith
                // eventPublisher.publishEvent(new PaymentCompletedEvent(orderCode, "SUCCESS",
                // "VNPAY"));

            } else {
                log.warn("VNPay IPN: Payment Failed for Order: {} with code: {}", orderCode, responseCode);
                // eventPublisher.publishEvent(new PaymentCompletedEvent(orderCode, "FAILED",
                // "VNPAY"));
            }

            // 4. Trả về cho VNPay biết đã ghi nhận
            return "{\"RspCode\":\"00\",\"Message\":\"Confirm Success\"}";

        } catch (Exception e) {
            log.error("VNPay IPN processing error", e);
            return "{\"RspCode\":\"99\",\"Message\":\"Unknown error\"}";
        }
    }
}
