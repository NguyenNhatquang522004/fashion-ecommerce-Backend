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
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.Properties;

import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
@RequiredArgsConstructor
public class PostgresDataSourceConfig {

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

    @Bean(name = "entityManagerFactory")
    @Primary
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource);

        // 1. Quét toàn bộ các module trong hệ thống Modular Monolithic
        em.setPackagesToScan("io.github.nguyennhatquang.fashion");

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        // Tự động nhận diện Dialect dựa trên DB hiện tại, giảm lỗi config sai version
        vendorAdapter.setGenerateDdl(false);
        em.setJpaVendorAdapter(vendorAdapter);

        Properties props = new Properties();

        // 2. SQL & Naming Strategy (Đảm bảo mapping chuẩn Spring Boot)
        props.setProperty("hibernate.physical_naming_strategy",
                "org.springframework.boot.orm.jpa.hibernate.SpringPhysicalNamingStrategy");
        props.setProperty("hibernate.implicit_naming_strategy",
                "org.springframework.boot.orm.jpa.hibernate.SpringImplicitNamingStrategy");

        // 3. VŨ KHÍ TỐI THƯỢNG CHO FLASH SALE (Batching nâng cao)
        props.setProperty("hibernate.jdbc.batch_size", "50");
        props.setProperty("hibernate.order_inserts", "true");
        props.setProperty("hibernate.order_updates", "true");
        props.setProperty("hibernate.jdbc.batch_versioned_data", "true"); // Quan trọng cho Optimistic Locking

        // 4. Tối ưu hóa hiệu năng PostgreSQL
        props.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        props.setProperty("hibernate.jdbc.fetch_size", "100"); // Tránh tràn RAM khi query tập dữ liệu lớn

        // Giảm thiểu overhead khi startup bằng cách bỏ qua quét metadata không cần
        // thiết
        props.setProperty("hibernate.temp.use_jdbc_metadata_defaults", "false");

        // 5. Kiểm soát tính nhất quán (Kết hợp với Liquibase)
        props.setProperty("hibernate.hbm2ddl.auto", "validate");

        // 6. Chống lỗi LazyInitializationException trong một số trường hợp View
        props.setProperty("hibernate.enable_lazy_load_no_trans", "false"); // Luôn để false để đảm bảo Clean
                                                                           // Architecture

        em.setJpaProperties(props);
        return em;
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