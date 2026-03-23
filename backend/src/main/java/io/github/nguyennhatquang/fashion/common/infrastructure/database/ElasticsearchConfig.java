package io.github.nguyennhatquang.fashion.common.infrastructure.database;

import org.springframework.boot.elasticsearch.autoconfigure.ElasticsearchProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;
import org.springframework.data.elasticsearch.config.EnableElasticsearchAuditing;
import org.springframework.data.elasticsearch.core.convert.ElasticsearchCustomConversions;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableElasticsearchAuditing(auditorAwareRef = "keycloakAuditorAware") // Đã bổ sung Auditing
@RequiredArgsConstructor
public class ElasticsearchConfig extends ElasticsearchConfiguration {

    private final ElasticsearchProperties elasticsearchProperties;

    @Override
    @NonNull
    public ClientConfiguration clientConfiguration() {
        // Tối ưu hóa việc lấy URIs từ properties
        String[] uris = elasticsearchProperties.getUris().stream()
                .map(uri -> uri.replace("http://", "").replace("https://", ""))
                .toArray(String[]::new);

        ClientConfiguration.MaybeSecureClientConfigurationBuilder builder = ClientConfiguration.builder()
                .connectedTo(uris);

        // Best Practice: Timeout cực kỳ quan trọng để tránh treo thread khi ES quá tải
        if (elasticsearchProperties.getConnectionTimeout() != null) {
            builder.withConnectTimeout(elasticsearchProperties.getConnectionTimeout());
        }
        if (elasticsearchProperties.getSocketTimeout() != null) {
            builder.withSocketTimeout(elasticsearchProperties.getSocketTimeout());
        }

        // Cấu hình bảo mật
        if (elasticsearchProperties.getUsername() != null && elasticsearchProperties.getPassword() != null) {
            builder.withBasicAuth(elasticsearchProperties.getUsername(), elasticsearchProperties.getPassword());
        }

        return builder.build();
    }

    /**
     * Best Practice: Đảm bảo Java 8 Dates (LocalDateTime) được lưu đúng định dạng
     * ISO-8601.
     * Giúp Elasticsearch hiểu đây là kiểu dữ liệu DATE để thực hiện
     * Search/Sort/Filter theo thời gian.
     */
    @Bean
    @Override
    public @NonNull ElasticsearchCustomConversions elasticsearchCustomConversions() {
        List<Object> converters = new ArrayList<>();
        // Bạn có thể thêm các custom converter đặc thù của ngành thời trang tại đây
        return new ElasticsearchCustomConversions(converters);
    }
}