package io.github.nguyennhatquang.fashion.common.mail;

import java.io.File;
import java.util.List;

import jakarta.annotation.Nullable;

public record EmailMessage(
        String to,
        String subject,
        String body,
        boolean isHtml,
        @Nullable
        List<File> attachments) {
    // Factory method cho text thường
    public static EmailMessage ofText(String to, String subject, String body) {
        return new EmailMessage(to, subject, body, false, null);
    }

    // Factory method cho HTML
    public static EmailMessage ofHtml(String to, String subject, String htmlBody) {
        return new EmailMessage(to, subject, htmlBody, true, null);
    }
}