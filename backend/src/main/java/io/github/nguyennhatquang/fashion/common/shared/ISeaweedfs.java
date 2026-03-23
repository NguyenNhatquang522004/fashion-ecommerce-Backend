package io.github.nguyennhatquang.fashion.common.shared;

import java.io.InputStream;


public interface ISeaweedfs {
    String uploadFile(String fileName, InputStream inputStream, String contentType, long contentLength);

    void deleteFile(String fileName);

    String getFileUrl(String fileName);
}
