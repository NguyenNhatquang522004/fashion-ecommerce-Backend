package io.github.nguyennhatquang.fashion.Order.configuration.Vnpay;

import java.util.Map;

public interface IVnpay {
    String generateVnPayUrl(long amount, String orderCode, String ipAddress);

    String processVnPayIpn(Map<String, String> params);
}
