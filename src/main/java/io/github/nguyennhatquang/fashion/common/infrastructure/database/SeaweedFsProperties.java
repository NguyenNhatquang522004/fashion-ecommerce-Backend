package io.github.nguyennhatquang.fashion.common.infrastructure.database;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "seaweedfs.s3")
public record SeaweedFsProperties(String endpoint, String region, String accessKey, String secretKey, String bucket) {
}