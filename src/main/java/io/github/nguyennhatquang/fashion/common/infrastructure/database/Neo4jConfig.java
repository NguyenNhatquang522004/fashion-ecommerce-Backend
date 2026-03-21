package io.github.nguyennhatquang.fashion.common.infrastructure.database;

import org.neo4j.cypherdsl.core.renderer.Configuration;
import org.neo4j.cypherdsl.core.renderer.Dialect;
import org.neo4j.driver.Driver;
import org.springframework.context.annotation.Bean;
import org.springframework.data.neo4j.config.EnableNeo4jAuditing;
import org.springframework.data.neo4j.core.DatabaseSelectionProvider;
import org.springframework.data.neo4j.core.transaction.Neo4jTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@org.springframework.context.annotation.Configuration
@EnableTransactionManagement
@EnableNeo4jAuditing // Best Practice: Bật Auditing toàn cục cho các module khác dùng
public class Neo4jConfig {

    /**
     * Best Practice: Cấu hình Transaction Manager rõ ràng cho Neo4j.
     * Rất quan trọng trong Modular Monolith nếu sau này bạn mix nhiều loại DB
     * (Neo4j + Postgres).
     */
    @Bean("neo4jTransactionManager")
    public Neo4jTransactionManager neo4jTransactionManager(Driver driver,
            DatabaseSelectionProvider databaseNameProvider) {
        return new Neo4jTransactionManager(driver, databaseNameProvider);
    }

    /**
     * Best Practice: Ép kiểu Cypher-DSL Dialect cho Neo4j 5.
     * Giúp Spring Data Neo4j generate ra các câu query Cypher tối ưu nhất cho phiên
     * bản DB hiện tại.
     */
    @Bean
    public Configuration cypherDslConfiguration() {
        return Configuration.newConfig()
                .withDialect(Dialect.NEO4J_5) // Đổi thành NEO4J_4 nếu bạn dùng bản cũ hơn
                .build();
    }
}