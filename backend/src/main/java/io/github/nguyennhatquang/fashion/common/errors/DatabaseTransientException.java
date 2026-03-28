package io.github.nguyennhatquang.fashion.common.errors;

public class DatabaseTransientException extends RuntimeException {
    // Constructor 1: Chỉ nhận thông báo lỗi (Dùng để sửa lỗi bạn vừa gặp)
    public DatabaseTransientException(String message) {
        super(message);
    }

    // Constructor 2: Nhận thông báo lỗi và nguyên nhân gốc (Exception cũ)
    public DatabaseTransientException(String message, Throwable cause) {
        super(message, cause);
    }

    // Constructor 3: (Tùy chọn) Chỉ nhận nguyên nhân gốc
    public DatabaseTransientException(Throwable cause) {
        super(cause);
    }
}
