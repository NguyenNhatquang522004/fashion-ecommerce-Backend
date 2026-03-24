package io.github.nguyennhatquang.fashion.common.response;

public record Result<T, E>(T data, E error) {
    public static <T, E> Result<T, E> success(T data) {
        return new Result<>(data, null);
    }

    public static <T, E> Result<T, E> error(E error) {
        return new Result<>(null, error);
    }

 
    public boolean hasError() {
        return error != null;
    }
}
