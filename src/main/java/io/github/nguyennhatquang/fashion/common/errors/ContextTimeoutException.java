package io.github.nguyennhatquang.fashion.common.errors;

public class ContextTimeoutException extends RuntimeException {
    public ContextTimeoutException(String message) {
        super(message);
    }

    public ContextTimeoutException(String message, Throwable cause) {
        super(message, cause);
    }
}
