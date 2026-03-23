package io.github.nguyennhatquang.fashion.Identity.configuration;

import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "keycloakAuditorAware")
@EnableJpaRepositories(basePackages = "io.github.nguyennhatquang.fashion.Identity.infrastructure.postgres.repository", entityManagerFactoryRef = "entityManagerFactory", transactionManagerRef = "postgresTransactionManager")
@EntityScan(basePackages = "io.github.nguyennhatquang.fashion.Identity.domain.entity")
public class IdentitypostgresConfig {

}
