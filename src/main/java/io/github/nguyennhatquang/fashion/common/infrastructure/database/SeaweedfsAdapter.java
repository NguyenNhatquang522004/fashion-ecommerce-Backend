package io.github.nguyennhatquang.fashion.common.infrastructure.database;

import java.io.InputStream;

import org.springframework.stereotype.Service;

import io.github.nguyennhatquang.fashion.common.shared.ISeaweedfs;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
public class SeaweedfsAdapter implements ISeaweedfs {
    private final S3Client s3Client;
    private final String bucketName;
    private final String endpoint;

    public SeaweedfsAdapter(S3Client s3Client, SeaweedFsProperties properties) {
        this.s3Client = s3Client;
        this.bucketName = properties.bucket();
        this.endpoint = properties.endpoint();
    }

    @Override
    public String uploadFile(String fileName, InputStream inputStream, String contentType, long contentLength) {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder().bucket(bucketName).key(fileName)
                .contentType(contentType).build();

        s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(inputStream, contentLength));
        return getFileUrl(fileName);
    }

    @Override
    public void deleteFile(String fileName) {
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder().bucket(bucketName).key(fileName)
                .build();

        s3Client.deleteObject(deleteObjectRequest);
    }

    @Override
    public String getFileUrl(String fileName) {
        // Trả về URL public tĩnh để các client có thể access trực tiếp tới SeaweedFS
        // (nếu public bucket)
        // Nếu dùng presigned URL thì cần cấu hình thêm S3Presigner
        return String.format("%s/%s/%s", endpoint, bucketName, fileName);
    }
}
