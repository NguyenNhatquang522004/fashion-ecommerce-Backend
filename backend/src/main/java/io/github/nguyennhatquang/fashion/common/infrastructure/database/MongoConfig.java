package io.github.nguyennhatquang.fashion.common.infrastructure.database;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.core.mapping.event.ValidatingMongoEventListener;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.Collections;

/**
 * Cấu hình hạ tầng MongoDB dùng chung cho toàn bộ hệ thống. Cần đảm bảo file
 * package-info.java của module common đã expose package này.
 */
@Configuration
@EnableMongoAuditing(auditorAwareRef = "keycloakAuditorAware") // Best Practice: Tự động điền @CreatedDate,
                                                               // @LastModifiedDate cho toàn bộ hệ
// thống
public class MongoConfig {
    // @Transactional("mongoTransactionManager")
    /**
     * Best Practice 1: Bật Transaction Manager. Bắt buộc phải có để hỗ trợ tính
     * chất ACID (yêu cầu MongoDB cấu hình Replica Set). Rất quan trọng cho các
     * nghiệp vụ phức tạp đòi hỏi tính nhất quán dữ liệu cao.
     */
    @Bean("mongoTransactionManager")
    public MongoTransactionManager transactionManager(MongoDatabaseFactory dbFactory) {
        return new MongoTransactionManager(dbFactory);
    }

    /**
     * Best Practice 2: Cấu hình Custom Conversions. Chuẩn bị sẵn Bean này để xử lý
     * việc mapping các Data Type phức tạp (ví dụ: ZonedDateTime, YearMonth) giữa
     * Java và BSON mà MongoDB không mặc định hỗ trợ.
     */
    @Bean
    public MongoCustomConversions customConversions() {
        // Thêm các converter tùy chỉnh vào Collections.emptyList() khi dự án mở rộng
        return new MongoCustomConversions(Collections.emptyList());
    }

    @Bean
    public ValidatingMongoEventListener validatingMongoEventListener(LocalValidatorFactoryBean factory) {
        return new ValidatingMongoEventListener(factory);
    }
}