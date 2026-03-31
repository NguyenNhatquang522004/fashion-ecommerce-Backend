package io.github.nguyennhatquang.fashion.common.cron;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import io.github.nguyennhatquang.fashion.common.shared.ICronJobScheduler;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DefaultCronJobScheduler implements ICronJobScheduler {
    private final TaskScheduler taskScheduler;
    private final Map<String, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();

    @Override
    public void scheduleJob(String jobId, Runnable task, String cronExpression) {
        if (scheduledTasks.containsKey(jobId)) {
            cancelJob(jobId);
        }
        ScheduledFuture<?> future = taskScheduler.schedule(task, new CronTrigger(cronExpression));
        scheduledTasks.put(jobId, future);
    }

    @Override
    public void cancelJob(String jobId) {
        ScheduledFuture<?> future = scheduledTasks.remove(jobId);
        if (future != null) {
            future.cancel(false);
        }
    }
}
