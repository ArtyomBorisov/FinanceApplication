package by.itacademy.report.config;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinioConfig {

    private final String accessKey;
    private final String secretKey;
    private final String minioUrl;

    public MinioConfig(@Value("${minio.access-key}") String accessKey,
                       @Value("${minio.secret-key}") String secretKey,
                       @Value("${minio.url}") String minioUrl) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
        this.minioUrl = minioUrl;
    }

    @Bean
    public MinioClient minioClient() {
        return new MinioClient.Builder()
                .credentials(accessKey, secretKey)
                .endpoint(minioUrl)
                .build();
    }
}
