package io.github.nguyennhatquang.fashion.common.infrastructure.seaweedfs;

import java.io.InputStream;
import java.time.Duration;

import org.springframework.stereotype.Service;

import io.github.nguyennhatquang.fashion.common.shared.ISeaweedfs;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.NoSuchBucketException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@Service
public class SeaweedfsAdapter implements ISeaweedfs {
    private final S3Client s3Client;
    private final String bucketName;
    private final String filerEndpoint;
    private final S3Presigner s3Presigner;

    public SeaweedfsAdapter(S3Client s3Client, SeaweedFsProperties properties, S3Presigner s3Presigner) {
        this.s3Client = s3Client;
        this.bucketName = properties.bucket();
        this.filerEndpoint = properties.filerEndpoint(); // Lấy Filer endpoint
        this.s3Presigner = s3Presigner;
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
            // Các lỗi khác (ví dụ chưa bật Docker, sai credentials...) có thể throw hoặc
            // log ra tùy bạn
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
        // Tối ưu nhất: Trả về link của Filer (port 8888) theo cấu trúc thư mục của
        // SeaweedFS
        // Ví dụ: http://localhost:8888/buckets/my-fashion-bucket/hinh-ao-thun.png
        return String.format("%s/buckets/%s/%s", filerEndpoint, bucketName, fileName);
    }

    public String generatePresignedUploadUrl(String fileName, String contentType) {
        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .contentType(contentType)
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(10)) // Link có hiệu lực trong 10 phút
                .putObjectRequest(objectRequest)
                .build();

        PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(presignRequest);
        return presignedRequest.url().toString();
    }

    /**
     * Tạo URL để Client Download file (Dùng cho các file riêng tư/Private)
     */
    public String generatePresignedDownloadUrl(String fileName) {
        GetObjectRequest objectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(60)) // Link xem ảnh/video trong 1 giờ
                .getObjectRequest(objectRequest)
                .build();

        PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(presignRequest);
        return presignedRequest.url().toString();
    }
}
