package io.github.nguyennhatquang.fashion.common.errors;

public class DatabaseTransientException extends RuntimeException {
    public DatabaseTransientException(String message, Throwable cause) {
        super(message, cause);
    }
}
