package io.github.nguyennhatquang.fashion.common.infrastructure.database;

import com.datastax.oss.driver.api.core.config.DefaultDriverOption;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.cassandra.config.EnableCassandraAuditing;

import java.time.Duration;

import org.springframework.boot.cassandra.autoconfigure.DriverConfigLoaderBuilderCustomizer;

/**
 * Cấu hình Cassandra Infrastructure cho toàn bộ hệ thống Modular Monolithic.
 * Nằm ở layer ngoài cùng (Frameworks & Drivers) trong Clean Architecture.
 */
@Configuration
@EnableCassandraAuditing // Kích hoạt Auditing cho các Entity ở các module khác
public class CassandraInfrastructureConfig {

    /**
     * Best Practice Spring Boot 3.x:
     * Sử dụng DriverConfigLoaderBuilderCustomizer để tuning DataStax Java Driver v4
     * thay vì extends AbstractCassandraConfiguration. Điều này giữ lại sức mạnh
     * auto-config của Spring.
     */
    @Bean("cassandraDriverCustomizer")
    public DriverConfigLoaderBuilderCustomizer cassandraDriverCustomizer() {
        return builder -> builder
                // 1. Tối ưu Timeout cho các truy vấn chậm (tránh làm nghẽn thread pool)
                .withDuration(DefaultDriverOption.CONNECTION_INIT_QUERY_TIMEOUT, Duration.ofSeconds(5))
                .withDuration(DefaultDriverOption.REQUEST_TIMEOUT, Duration.ofSeconds(2))

                // 2. Tối ưu Connection Pooling (Dành cho High Concurrency)
                // Số lượng connection giữ kết nối tới mỗi node trong local Datacenter
                .withInt(DefaultDriverOption.CONNECTION_POOL_LOCAL_SIZE, 4)
                // Số lượng connection tới remote Datacenter (nếu có Multi-DC)
                .withInt(DefaultDriverOption.CONNECTION_POOL_REMOTE_SIZE, 1)

                // 3. Retry Policy: Sử dụng Default để an toàn, tự động retry khi bị timeout do
                // network
                .withString(DefaultDriverOption.RETRY_POLICY_CLASS, "DefaultRetryPolicy")

                // 4. Load Balancing: Cấm failover sang Datacenter khác đối với các query yêu
                // cầu Local Consistency
                // Giúp tránh tình trạng read/write chéo region làm tăng latency đột biến

                // 5. Cấu hình kích thước max requests trên mỗi connection (chuẩn DataStax v4)
                .withInt(DefaultDriverOption.CONNECTION_MAX_REQUESTS, 1024);
    }
}