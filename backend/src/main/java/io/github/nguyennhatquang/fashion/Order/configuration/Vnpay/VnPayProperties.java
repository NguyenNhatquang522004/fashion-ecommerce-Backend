package io.github.nguyennhatquang.fashion.Order.configuration.Vnpay;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
@ConfigurationProperties(prefix = "vnpay")
public class VnPayProperties {
    private String tmnCode;
    private String secretKey;
    private String payUrl;
    private String returnUrl;
    private String version;
    private String command;
}
