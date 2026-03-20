package io.github.nguyennhatquang.fashion.common.infrastructure.database;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Configuration
@ConfigurationProperties(prefix = "common.datasource.hikari")
@Getter
@Setter
public class CommonHikariProperties {
    private String jdbcUrl;
    private String username;
    private String password;
    private String driverClassName;
    private String poolName;

    // Khởi tạo sẵn giá trị default luôn để đảm bảo an toàn nếu YAML bị thiếu
    private int minimumIdle = 10;
    private int maximumPoolSize = 50;
    private long connectionTimeout = 3000;
    private long idleTimeout = 600000;
    private long maxLifetime = 1800000;
    private boolean autoCommit = false;
}
