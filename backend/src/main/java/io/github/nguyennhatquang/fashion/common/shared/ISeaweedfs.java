package io.github.nguyennhatquang.fashion.common.shared;

import java.io.InputStream;

import io.github.nguyennhatquang.fashion.common.Enum.StorageFolderEnum;

public interface ISeaweedfs {
    String uploadFile(StorageFolderEnum folder, String ownerId, String originalFileName,
            InputStream inputStream, String contentType, long contentLength);

    String generatePresignedUploadUrl(StorageFolderEnum folder, String ownerId,
            String originalFileName, String contentType);

    void deleteFile(String fileKeyOrUrl);

    String generatePresignedDownloadUrl(String fileKeyOrUrl);

    String getFileUrl(String fileKeyOrUrl);
}
