# Populate `StorageFolderEnum` for SeaweedFS

The empty [StorageFolderEnum.java](file:///d:/BlazeAndRiser/project-Three/fashion-ecommerce/backend/src/main/java/io/github/nguyennhatquang/fashion/common/Enum/StorageFolderEnum.java) needs folder-path constants so every module can build S3 keys consistently when uploading/downloading files via the [SeaweedfsAdapter](file:///d:/BlazeAndRiser/project-Three/fashion-ecommerce/backend/src/main/java/io/github/nguyennhatquang/fashion/common/infrastructure/seaweedfs/SeaweedfsAdapter.java).

## Analysis — Entities That Store Media

| Module | Entity / Collection | Field(s) | Media Type |
|---|---|---|---|
| Identity | `user_profiles` (PG) | `avatar_url` | User avatar images |
| Catalog | `categories` (Mongo) | `image_url` | Category icons/banners |
| Catalog | `brands` (Mongo) | `logo_url` | Brand logos |
| Catalog | `products.media[]` (Mongo) | `url`, `type` (image/video) | Product photos & videos |
| Catalog | `products.variants[].media[]` (Mongo) | `url`, `type` | SKU-specific variant media |
| Personalization | `style_guides` (Mongo) | `cover_image_url` | Style-guide cover images |
| Order | `flash_sale_campaigns` (PG) | `banner_url` | Campaign banners |
| Notification | `in_app_notifications` (Mongo) | `image_url` | Notification images |

## Proposed Changes

### common / Enum

#### [MODIFY] [StorageFolderEnum.java](file:///d:/BlazeAndRiser/project-Three/fashion-ecommerce/backend/src/main/java/io/github/nguyennhatquang/fashion/common/Enum/StorageFolderEnum.java)

Follow the exact same pattern used by [ProductMediaTypeEnum](file:///d:/BlazeAndRiser/project-Three/fashion-ecommerce/backend/src/main/java/io/github/nguyennhatquang/fashion/common/Enum/ProductMediaTypeEnum.java) and [CampaignStatusEnum](file:///d:/BlazeAndRiser/project-Three/fashion-ecommerce/backend/src/main/java/io/github/nguyennhatquang/fashion/common/Enum/CampaignStatusEnum.java):

- `@Getter @RequiredArgsConstructor` with `private final String value`
- `@JsonValue` / `@JsonCreator` + case-insensitive [fromValue()](file:///d:/BlazeAndRiser/project-Three/fashion-ecommerce/backend/src/main/java/io/github/nguyennhatquang/fashion/common/Enum/ProductMediaTypeEnum.java#31-39)
- Helper `buildKey(String fileName)` that returns `"<folder>/<fileName>"` for direct use in `ISeaweedfs.uploadFile(key, …)`

**Constants** (8 folders, one per media domain):

```java
AVATARS("avatars"),                     // user_profiles.avatar_url
CATEGORY_IMAGES("categories"),          // categories.image_url
BRAND_LOGOS("brands"),                  // brands.logo_url
PRODUCT_MEDIA("products"),              // products.media[].url
VARIANT_MEDIA("variants"),             // products.variants[].media[].url
STYLE_GUIDE_COVERS("style-guides"),    // style_guides.cover_image_url
CAMPAIGN_BANNERS("campaigns"),         // flash_sale_campaigns.banner_url
NOTIFICATION_IMAGES("notifications")   // in_app_notifications.image_url
```

> [!NOTE]
> The `value` string becomes the first path segment of the S3 key.
> Example: `StorageFolderEnum.PRODUCT_MEDIA.buildKey("abc-123.jpg")` → `"products/abc-123.jpg"`.
> The adapter will prepend the bucket: `http://filer:8888/buckets/fashion-catalog-media/products/abc-123.jpg`.

## Verification Plan

### Automated Tests

The project currently has no unit tests beyond the default Spring Boot context test. Compilation is the primary gate:

```powershell
cd d:\BlazeAndRiser\project-Three\fashion-ecommerce\backend
mvn compile -pl . -q
```

If this succeeds without errors, the enum is syntactically correct and all imports resolve.

### Manual Verification

1. Open the file in your IDE and confirm IntelliSense resolves `StorageFolderEnum.PRODUCT_MEDIA.getValue()` → `"products"` and `StorageFolderEnum.PRODUCT_MEDIA.buildKey("img.jpg")` → `"products/img.jpg"`.
2. Verify no existing code references `StorageFolderEnum` (confirmed — only the enum declaration itself), so there's zero regression risk.
