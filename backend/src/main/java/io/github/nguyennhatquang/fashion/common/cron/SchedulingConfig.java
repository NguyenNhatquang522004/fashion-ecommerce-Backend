package io.github.nguyennhatquang.fashion.common.cron;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;

import io.github.nguyennhatquang.fashion.common.errors.GlobalSchedulingErrorHandler;

@Configuration
@EnableScheduling
public class SchedulingConfig {
    // Inject bean virtualThreadExecutor của bạn vào đây
    @Bean
    TaskScheduler virtualTaskScheduler(@Qualifier("virtualThreadExecutor") ExecutorService virtualThreadExecutor) {

        // 1. Tạo 1 Platform Thread chạy ngầm (Daemon) CHỈ để canh giờ Cron
        ScheduledExecutorService timeTracker = Executors.newSingleThreadScheduledExecutor(runnable -> {
            Thread thread = new Thread(runnable, "cron-trigger-thread");
            thread.setDaemon(true);
            return thread;
        });

        // 2. Delegate: Khi đến giờ, giao task cho Virtual Thread thực thi
        ConcurrentTaskScheduler scheduler = new ConcurrentTaskScheduler(virtualThreadExecutor, timeTracker);
        scheduler.setErrorHandler(new GlobalSchedulingErrorHandler());

        return scheduler;
    }
}
