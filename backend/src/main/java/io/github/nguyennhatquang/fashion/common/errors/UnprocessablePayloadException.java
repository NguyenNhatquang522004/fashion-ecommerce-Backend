package io.github.nguyennhatquang.fashion.common.errors;

public class UnprocessablePayloadException extends RuntimeException {
    public UnprocessablePayloadException(String message, Throwable cause) {
        super(message, cause);
    }
}
