package io.github.nguyennhatquang.fashion.common.infrastructure.auth;

public class UserContextHolder {
    // ThreadLocal giúp mỗi Thread giữ một biến riêng biệt, không giẫm chân lên nhau
    private static final ThreadLocal<String> CONTEXT = new ThreadLocal<>();

    public static void setUserId(String userId) {
        CONTEXT.set(userId);
    }

    public static String getUserId() {
        return CONTEXT.get();
    }

    public static void clear() {
        CONTEXT.remove(); // BẮT BUỘC CÓ: Để chống rò rỉ bộ nhớ (Memory Leak)
    }
}