package io.github.nguyennhatquang.fashion.Order.configuration.Momo;

import io.github.nguyennhatquang.fashion.Order.delivery.Dto.Momo.MomoRequest.MoMoIpnRequest;

public interface IMomo {
    String createPaymentUrl(long amount, String orderCode);
    void processIpn(MoMoIpnRequest ipnRequest);
}
