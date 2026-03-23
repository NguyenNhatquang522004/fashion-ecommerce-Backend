package io.github.nguyennhatquang.fashion.common.mail;

import java.io.File;
import java.nio.charset.StandardCharsets;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import io.github.nguyennhatquang.fashion.common.shared.IEmail;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailAdapter implements IEmail {

    private final JavaMailSender mailSender;

    @Async // Rất quan trọng: Chạy trên thread pool riêng
    @Override
    public void send(EmailMessage message) {
        try {
            log.info("Bắt đầu gửi email tới: {}", message.to());
            MimeMessage mimeMessage = mailSender.createMimeMessage();

            // Sử dụng MimeMessageHelper hỗ trợ UTF-8 và Multipart (Attachment)
            MimeMessageHelper helper = new MimeMessageHelper(
                    mimeMessage,
                    message.attachments() != null && !message.attachments().isEmpty(),
                    StandardCharsets.UTF_8.name());

            helper.setTo(message.to());
            helper.setSubject(message.subject());
            helper.setText(message.body(), message.isHtml()); // true nếu là HTML

            // Xử lý đính kèm nếu có
            if (message.attachments() != null) {
                for (File file : message.attachments()) {
                    helper.addAttachment(file.getName(), file);
                }
            }

            mailSender.send(mimeMessage);
            log.info("Đã gửi email thành công tới: {}", message.to());

        } catch (MessagingException e) {
            log.error("Lỗi khi tạo MimeMessage cho email tới: {}", message.to(), e);
            // Trong Clean Architecture, bạn có thể quăng ra một Custom Exception thuộc
            // Domain
            // Hoặc lưu log lại vào database để tạo cơ chế retry (Dead Letter Queue)
        } catch (Exception e) {
            log.error("Lỗi không xác định khi gửi email tới: {}", message.to(), e);
        }
    }
}
