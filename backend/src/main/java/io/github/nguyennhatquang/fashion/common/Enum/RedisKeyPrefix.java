package io.github.nguyennhatquang.fashion.common.Enum;

public enum RedisKeyPrefix {
    EVENT_STATUS("event:status:"),
    EVENT_LOCK("event:lock:");

    private final String prefix;

    RedisKeyPrefix(String prefix) {
        this.prefix = prefix;
    }

    /**
     * Helper method nối chuỗi an toàn, chuẩn xác
     */
    public String append(String suffix) {
        return this.prefix + suffix;
    }
}
