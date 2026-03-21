package io.github.nguyennhatquang.fashion.common.infrastructure.database;

import java.io.InputStream;

import org.springframework.stereotype.Service;

import io.github.nguyennhatquang.fashion.common.shared.ISeaweedfs;
import jakarta.annotation.PostConstruct;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.NoSuchBucketException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
public class SeaweedfsAdapter implements ISeaweedfs {
 private final S3Client s3Client;
    private final String bucketName;
    private final String filerEndpoint;

    public SeaweedfsAdapter(S3Client s3Client, SeaweedFsProperties properties) {
        this.s3Client = s3Client;
        this.bucketName = properties.bucket();
        this.filerEndpoint = properties.filerEndpoint(); // Lấy Filer endpoint
    }

    // Tự động kiểm tra và tạo Bucket khi ứng dụng Spring Boot vừa khởi chạy xong
    @PostConstruct
    public void initBucketIfNotExists() {
        try {
            s3Client.headBucket(HeadBucketRequest.builder().bucket(bucketName).build());
        } catch (NoSuchBucketException e) {
            // Nếu bucket chưa tồn tại -> Tạo mới
            s3Client.createBucket(CreateBucketRequest.builder().bucket(bucketName).build());
        } catch (Exception e) {
            // Các lỗi khác (ví dụ chưa bật Docker, sai credentials...) có thể throw hoặc log ra tùy bạn
            throw new RuntimeException("Lỗi khi khởi tạo SeaweedFS Bucket: " + e.getMessage(), e);
        }
    }

    @Override
    public String uploadFile(String fileName, InputStream inputStream, String contentType, long contentLength) {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .contentType(contentType)
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(inputStream, contentLength));
        return getFileUrl(fileName);
    }

    @Override
    public void deleteFile(String fileName) {
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build();

        s3Client.deleteObject(deleteObjectRequest);
    }

    @Override
    public String getFileUrl(String fileName) {
        // Tối ưu nhất: Trả về link của Filer (port 8888) theo cấu trúc thư mục của SeaweedFS
        // Ví dụ: http://localhost:8888/buckets/my-fashion-bucket/hinh-ao-thun.png
        return String.format("%s/buckets/%s/%s", filerEndpoint, bucketName, fileName);
    }
}
