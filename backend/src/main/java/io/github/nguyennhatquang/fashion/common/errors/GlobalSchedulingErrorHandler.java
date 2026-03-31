package io.github.nguyennhatquang.fashion.common.errors;

import org.springframework.stereotype.Component;
import org.springframework.util.ErrorHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class GlobalSchedulingErrorHandler implements ErrorHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalSchedulingErrorHandler.class);

    @Override
    public void handleError(Throwable t) {
        log.error("Lỗi nghiêm trọng khi chạy Scheduled Task trên Virtual Thread", t);
        // Tích hợp đẩy log lỗi về Bugzilla hoặc ELK Stack tại đây
    }
}
