package io.github.nguyennhatquang.fashion.common.infrastructure.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
@RequiredArgsConstructor
public class PostgresDataSourceConfig {

    // Inject class properties bạn vừa tạo
    private final CommonHikariProperties properties;

    @Bean
    @Primary
    public DataSource dataSource() {
        HikariConfig config = new HikariConfig();

        config.setJdbcUrl(properties.getJdbcUrl());
        config.setUsername(properties.getUsername());
        config.setPassword(properties.getPassword());
        config.setDriverClassName(properties.getDriverClassName());
        config.setPoolName(properties.getPoolName());

        config.setMinimumIdle(properties.getMinimumIdle());
        config.setMaximumPoolSize(properties.getMaximumPoolSize());
        config.setConnectionTimeout(properties.getConnectionTimeout());
        config.setIdleTimeout(properties.getIdleTimeout());
        config.setMaxLifetime(properties.getMaxLifetime());
        config.setAutoCommit(properties.isAutoCommit());

        // =========================================================
        // 100% POSTGRESQL BEST PRACTICES (Đã dọn dẹp code MySQL)
        // =========================================================

        // 1. Vũ khí quan trọng nhất cho Flash Sale: Cho phép gộp lệnh Insert/Update
        // (Nếu thiếu dòng này, hibernate.jdbc.batch_size ở properties sẽ vô tác dụng
        // với Postgres)
        config.addDataSourceProperty("reWriteBatchedInserts", "true");

        // 2. Gắn tên App để dễ Monitor connection trên Database (khi query
        // pg_stat_activity)
        config.addDataSourceProperty("ApplicationName", "fashion-ecommerce-app");

        // 3. Giữ connection không bị rớt khi đi qua Firewall/Load Balancer
        config.addDataSourceProperty("tcpKeepAlive", "true");

        return new HikariDataSource(config);
    }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    public NamedParameterJdbcTemplate namedParameterJdbcTemplate(DataSource dataSource) {
        return new NamedParameterJdbcTemplate(dataSource);
    }

    @Bean("postgresTransactionManager")
    @Primary // Ưu tiên thằng này làm mặc định nếu dùng @Transactional không truyền tên
    public PlatformTransactionManager postgresTransactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
        // Nếu bạn dùng Spring Data JPA thay vì JdbcTemplate, hãy dùng
        // JpaTransactionManager
    }
}