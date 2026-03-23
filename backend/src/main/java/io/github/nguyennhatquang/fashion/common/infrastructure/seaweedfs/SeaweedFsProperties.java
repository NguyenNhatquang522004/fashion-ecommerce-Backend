package io.github.nguyennhatquang.fashion.common.infrastructure.seaweedfs;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "seaweedfs.s3")
public record SeaweedFsProperties(String endpoint, String filerEndpoint, String region, String accessKey,
        String secretKey, String bucket) {
}