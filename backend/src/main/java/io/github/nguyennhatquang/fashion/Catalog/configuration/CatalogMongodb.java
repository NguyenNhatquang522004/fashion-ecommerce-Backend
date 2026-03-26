package io.github.nguyennhatquang.fashion.Catalog.configuration;

import org.springframework.context.annotation.Configuration;

import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration  
@EnableMongoRepositories(basePackages = "io.github.nguyennhatquang.fashion.Catalog.domain.repository", mongoTemplateRef = "catalogMongoTemplate")
public class CatalogMongodb {

}
