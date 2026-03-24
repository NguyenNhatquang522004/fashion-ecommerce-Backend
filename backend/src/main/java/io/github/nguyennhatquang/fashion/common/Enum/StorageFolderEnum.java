package io.github.nguyennhatquang.fashion.common.Enum;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Getter
@RequiredArgsConstructor
public enum StorageFolderEnum {
    // 1. Module Identity
    USER_AVATAR("users/%s/avatars"),             // Pattern: users/{user_id}/avatars/

    // 2. Module Catalog
    CATEGORY_IMAGE("categories/%s/images"),      // Pattern: categories/{category_id}/images/
    BRAND_LOGO("brands/%s/logos"),               // Pattern: brands/{brand_id}/logos/
    PRODUCT_MEDIA("products/%s/media"),          // Pattern: products/{product_id}/media/
    VARIANT_MEDIA("sku-variants/%s/media"),      // Pattern: sku-variants/{sku_code}/media/

    // 3. Module Order & Marketing
    CAMPAIGN_BANNER("campaigns/%s/banners"),     // Pattern: campaigns/{campaign_id}/banners/

    // 4. Module Personalization (RAG / AI)
    STYLE_GUIDE_COVER("style-guides/%s/covers"), // Pattern: style-guides/{document_id}/covers/

    // 5. Module Notification
    NOTIFICATION_IMAGE("notifications/%s/images"),// Pattern: notifications/{notification_id}/images/

    // 6. System (Các file dùng chung của hệ thống, mặc định)
    SYSTEM_ASSET("system/assets");               // Pattern: system/assets/

    @JsonValue
    private final String pathFormat;

    /**
     * Dành cho Spring Boot tự động map từ Request
     */
    @JsonCreator
    public static StorageFolderEnum fromValue(String value) {
        for (StorageFolderEnum folder : values()) {
            if (folder.name().equalsIgnoreCase(value)) {
                return folder;
            }
        }
        throw new IllegalArgumentException("Unknown Storage Folder: " + value);
    }

    /**
     * BEST PRACTICE: Hàm tạo Key an toàn (Có Entity ID và Unique Filename)
     * * @param entityId ID của thực thể (Ví dụ: user_id, product_id). Truyền null nếu là System Asset.
     * @param originalFileName Tên file gốc từ Client tải lên
     * @return S3 Key hoàn chỉnh (Ví dụ: users/123-abc/avatars/550e8400...-avatar.png)
     */
    public String buildKey(String entityId, String originalFileName) {
        // 1. Làm sạch tên file (Tránh lỗi Path Traversal và ký tự lạ)
        String cleanName = (originalFileName != null) 
                ? originalFileName.replaceAll("[^a-zA-Z0-9.-]", "_") 
                : "unnamed_file";

        // 2. Đảm bảo tên file luôn duy nhất bằng UUID để không bao giờ ghi đè file cũ
        String uniqueFileName = UUID.randomUUID().toString() + "-" + cleanName;

        // 3. Ráp Path
        if (this == SYSTEM_ASSET) {
            return this.pathFormat + "/" + uniqueFileName;
        }

        if (entityId == null || entityId.trim().isEmpty()) {
            throw new IllegalArgumentException("Entity ID cannot be null or empty for folder type: " + this.name());
        }

        String folderPath = String.format(this.pathFormat, entityId);
        return folderPath + "/" + uniqueFileName;
    }
}