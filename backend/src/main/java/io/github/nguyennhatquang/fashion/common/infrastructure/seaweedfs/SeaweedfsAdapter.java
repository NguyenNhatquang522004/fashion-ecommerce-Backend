package io.github.nguyennhatquang.fashion.common.infrastructure.seaweedfs;

import java.io.InputStream;
import java.time.Duration;

import org.springframework.stereotype.Service;

import io.github.nguyennhatquang.fashion.common.Enum.StorageFolderEnum;
import io.github.nguyennhatquang.fashion.common.shared.ISeaweedfs;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.*;

@Slf4j
@Service
public class SeaweedfsAdapter implements ISeaweedfs {
    private final S3Client s3Client;
    private final String bucketName;
    private final String filerEndpoint;
    private final S3Presigner s3Presigner;

    public SeaweedfsAdapter(S3Client s3Client, SeaweedFsProperties properties, S3Presigner s3Presigner) {
        this.s3Client = s3Client;
        this.bucketName = properties.bucket();
        this.filerEndpoint = properties.filerEndpoint();
        this.s3Presigner = s3Presigner;
    }

    @PostConstruct
    public void initBucketIfNotExists() {
        try {
            s3Client.headBucket(HeadBucketRequest.builder().bucket(bucketName).build());
            log.info("SeaweedFS Bucket '{}' is ready.", bucketName);
        } catch (NoSuchBucketException e) {
            s3Client.createBucket(CreateBucketRequest.builder().bucket(bucketName).build());
            log.info("Created new SeaweedFS Bucket '{}'.", bucketName);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khởi tạo SeaweedFS Bucket: " + e.getMessage(), e);
        }
    }

    // =========================================================================
    // 1. NHÓM HÀM UPLOAD (Sử dụng StorageFolderEnum & ownerId để TẠO KEY MỚI)
    // =========================================================================

    @Override
    public String uploadFile(StorageFolderEnum folder, String ownerId, String originalFileName,
            InputStream inputStream, String contentType, long contentLength) {
        // 1. Sinh S3 Key tự động (VD: users/123/avatars/uuid-avatar.png)
        String s3Key = folder.buildKey(ownerId, originalFileName);

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(s3Key)
                .contentType(contentType)
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(inputStream, contentLength));

        // Trả về Full URL để lưu vào DB
        return getFileUrl(s3Key);
    }

    // output bao gồm presigned_url Client tự đẩy file lên SeaweedFS. final_url:
    // Đường dẫn gốc (Sạch) mà file sẽ nằm lại sau khi upload xong. (Để lát nữa
    // Client biết đường mà gửi lại cho Backend lưu Database).
    @Override
    public String generatePresignedUploadUrl(StorageFolderEnum folder, String ownerId,
            String originalFileName, String contentType) {
        // 1. Sinh S3 Key tự động
        String s3Key = folder.buildKey(ownerId, originalFileName);

        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(s3Key)
                .contentType(contentType)
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(15)) // Tăng lên 15 phút cho an toàn mạng chậm
                .putObjectRequest(objectRequest)
                .build();

        return s3Presigner.presignPutObject(presignRequest).url().toString();
    }

    // =========================================================================
    // 2. NHÓM HÀM ĐỌC / XÓA (Phải sử dụng chính xác S3 Key hoặc Full URL từ DB)
    // =========================================================================

    @Override
    public void deleteFile(String fileKeyOrUrl) {
        if (fileKeyOrUrl == null || fileKeyOrUrl.isBlank())
            return;

        // Thông minh: Tự bóc tách S3 Key nếu User truyền vào nguyên cái URL từ DB
        String s3Key = extractS3Key(fileKeyOrUrl);

        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(s3Key)
                .build();

        s3Client.deleteObject(deleteObjectRequest);
        log.info("Deleted file from SeaweedFS: {}", s3Key);
    }

    @Override
    public String getFileUrl(String fileKeyOrUrl) {
        String s3Key = extractS3Key(fileKeyOrUrl);
        return String.format("%s/buckets/%s/%s", filerEndpoint, bucketName, s3Key);
    }

    @Override
    public String generatePresignedDownloadUrl(String fileKeyOrUrl) {
        String s3Key = extractS3Key(fileKeyOrUrl);

        GetObjectRequest objectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(s3Key)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(60))
                .getObjectRequest(objectRequest)
                .build();

        return s3Presigner.presignGetObject(presignRequest).url().toString();
    }

    // =========================================================================
    // 3. HELPER METHOD (Logic bóc tách chuỗi)
    // =========================================================================

    /**
     * Hàm helper cực kỳ quan trọng:
     * Giúp hệ thống vẫn chạy đúng dù bạn truyền S3 Key "users/123/avatar.png"
     * hay truyền Full URL
     * "http://localhost:8888/buckets/my-bucket/users/123/avatar.png"
     */
    private String extractS3Key(String input) {
        if (input == null)
            return "";

        // Nếu là URL (bắt đầu bằng http hoặc chứa tên bucket)
        String bucketPrefix = "/buckets/" + bucketName + "/";
        if (input.contains(bucketPrefix)) {
            return input.substring(input.indexOf(bucketPrefix) + bucketPrefix.length());
        }

        // Xóa dấu slash ở đầu nếu có (VD: "/users/123" -> "users/123")
        return input.startsWith("/") ? input.substring(1) : input;
    }
}