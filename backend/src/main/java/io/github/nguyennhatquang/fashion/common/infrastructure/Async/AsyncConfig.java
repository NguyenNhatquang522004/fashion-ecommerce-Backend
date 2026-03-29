package io.github.nguyennhatquang.fashion.common.infrastructure.Async;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AsyncConfig {
    @Bean(name = "virtualThreadExecutor")
    public ExecutorService virtualThreadExecutor() {
        // Cách khởi tạo 100% Best Practice của Java 21
        return Executors.newVirtualThreadPerTaskExecutor();
    }
}
