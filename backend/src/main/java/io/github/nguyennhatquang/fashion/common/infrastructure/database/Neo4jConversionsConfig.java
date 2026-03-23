package io.github.nguyennhatquang.fashion.common.infrastructure.database;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.neo4j.core.convert.Neo4jConversions;
import java.util.Collections;

@Configuration
public class Neo4jConversionsConfig {
    /**
     * Nơi đăng ký các Custom Converter (từ Java Object -> Neo4j Value và ngược
     * lại).
     * Các module khác có thể tận dụng base config này.
     */
    @Bean
    public Neo4jConversions neo4jCustomConversions() {
        // Thêm các custom converter vào List Collections.emptyList() khi cần
        return new Neo4jConversions(Collections.emptyList());
    }
}
