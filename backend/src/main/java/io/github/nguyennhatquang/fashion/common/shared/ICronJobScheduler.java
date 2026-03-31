package io.github.nguyennhatquang.fashion.common.shared;

public interface ICronJobScheduler {
    /**
     * Đăng ký một cron job mới.
     * 
     * @param jobId          ID duy nhất của job để quản lý.
     * @param task           Nhiệm vụ cần thực thi (thường là gọi Use Case).
     * @param cronExpression Biểu thức cron (VD: "0 0 2 * * ?").
     */
    void scheduleJob(String jobId, Runnable task, String cronExpression);

    /**
     * Hủy một job đang chạy.
     */
    void cancelJob(String jobId);
}
