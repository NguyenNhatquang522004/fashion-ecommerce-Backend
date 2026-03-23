package io.github.nguyennhatquang.fashion.common.shared;

import io.github.nguyennhatquang.fashion.common.mail.EmailMessage;

public interface IEmail {
    /**
     * Gửi email bất đồng bộ.
     * @param message Thông tin email cần gửi
     */
    void send(EmailMessage message);
}
