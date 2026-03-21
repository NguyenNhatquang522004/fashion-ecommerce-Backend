package io.github.nguyennhatquang.fashion.common.infrastructure.database;

import org.springframework.boot.elasticsearch.autoconfigure.ElasticsearchProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;

import lombok.NonNull;

@Configuration
public class ElasticsearchConfig extends ElasticsearchConfiguration {
    private final ElasticsearchProperties elasticsearchProperties;

    // Inject tự động các cấu hình từ application.properties thông qua class chuẩn
    // của Spring
    public ElasticsearchConfig(ElasticsearchProperties elasticsearchProperties) {
        this.elasticsearchProperties = elasticsearchProperties;
    }

    @Override
    @NonNull
    public ClientConfiguration clientConfiguration() {
        // 1. Lấy và format lại mảng URIs (loại bỏ http/https vì
        // ClientConfiguration.builder yêu cầu format host:port)
        String[] uris = elasticsearchProperties.getUris().stream()
                .map(uri -> uri.replace("http://", "").replace("https://", ""))
                .toArray(String[]::new);

        ClientConfiguration.MaybeSecureClientConfigurationBuilder builder = ClientConfiguration.builder()
                .connectedTo(uris);

        // 2. Cấu hình Timeout (Rất quan trọng cho hệ thống Production / High
        // Concurrency)
        if (elasticsearchProperties.getConnectionTimeout() != null) {
            builder.withConnectTimeout(elasticsearchProperties.getConnectionTimeout());
        }
        if (elasticsearchProperties.getSocketTimeout() != null) {
            builder.withSocketTimeout(elasticsearchProperties.getSocketTimeout());
        }

        // 3. Cấu hình Authentication
        if (elasticsearchProperties.getUsername() != null && elasticsearchProperties.getPassword() != null) {
            builder.withBasicAuth(elasticsearchProperties.getUsername(), elasticsearchProperties.getPassword());
        }

        // 4. (Tùy chọn tương lai) Cấu hình SSL, Default Headers, Proxy... có thể add
        // thêm tại đây

        return builder.build();
    }
}
