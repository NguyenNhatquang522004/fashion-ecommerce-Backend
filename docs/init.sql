-- =========================================================================
-- 1. TẠO CÁC KIỂU DỮ LIỆU ENUM (Đặc thù của PostgreSQL)
-- Giúp data integrity tốt hơn và tiết kiệm dung lượng so với VARCHAR
-- =========================================================================

CREATE TYPE gender_enum AS ENUM ('MALE', 'FEMALE', 'UNISEX', 'OTHER');
CREATE TYPE loyalty_tier_enum AS ENUM ('BRONZE', 'SILVER', 'GOLD', 'PLATINUM');
CREATE TYPE profile_status_enum AS ENUM ('ACTIVE', 'INACTIVE', 'BANNED');
CREATE TYPE address_type_enum AS ENUM ('HOME', 'OFFICE', 'OTHER');

-- =========================================================================
-- 2. TẠO BẢNG USER_PROFILES (Shadow Entity của Keycloak)
-- =========================================================================

CREATE TABLE user_profiles (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    keycloak_id VARCHAR(36) NOT NULL UNIQUE,
    email VARCHAR(255) UNIQUE,
    full_name VARCHAR(150) NOT NULL,
    phone_number VARCHAR(20) UNIQUE,
    avatar_url VARCHAR(500),
    dob DATE,
    gender gender_enum,
    loyalty_tier loyalty_tier_enum DEFAULT 'BRONZE',
    status profile_status_enum DEFAULT 'ACTIVE',
    
    -- Optimistic Locking
    version BIGINT DEFAULT 0,
    
    -- Auditing fields
    created_by VARCHAR(255),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(255),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    
    -- Soft Delete
    is_deleted BOOLEAN DEFAULT FALSE
);

-- Tạo Index cho các trường thường xuyên được Query (Tìm kiếm, Đăng nhập phụ)
CREATE INDEX idx_user_profiles_keycloak_id ON user_profiles(keycloak_id);
CREATE INDEX idx_user_profiles_email ON user_profiles(email);
CREATE INDEX idx_user_profiles_phone_number ON user_profiles(phone_number);

-- =========================================================================
-- 3. TẠO BẢNG USER_ADDRESSES (Sổ địa chỉ của khách hàng)
-- =========================================================================

CREATE TABLE user_addresses (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    user_profile_id UUID NOT NULL,
    receiver_name VARCHAR(150) NOT NULL,
    receiver_phone VARCHAR(20) NOT NULL,
    street_line VARCHAR(255) NOT NULL,
    
    -- Hành chính (Bắt buộc dùng Code để map API Giao hàng)
    ward_code VARCHAR(20) NOT NULL,
    ward_name VARCHAR(100) NOT NULL,
    district_code VARCHAR(20) NOT NULL,
    district_name VARCHAR(100) NOT NULL,
    province_code VARCHAR(20) NOT NULL,
    province_name VARCHAR(100) NOT NULL,
    
    -- Tọa độ
    latitude DOUBLE PRECISION,
    longitude DOUBLE PRECISION,
    
    -- Phân loại & Mặc định
    address_type address_type_enum,
    is_default BOOLEAN DEFAULT FALSE,
    
    -- Auditing & Soft Delete
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    is_deleted BOOLEAN DEFAULT FALSE,
    
    -- Ràng buộc Khóa ngoại (Foreign Key)
    CONSTRAINT fk_user_address_profile 
      FOREIGN KEY (user_profile_id) 
      REFERENCES user_profiles(id) 
      ON DELETE RESTRICT -- Không cho phép hard delete user nếu còn địa chỉ
);

-- Tạo Index cho khóa ngoại để tối ưu khi query "Lấy danh sách địa chỉ của User X"
CREATE INDEX idx_user_addresses_profile_id ON user_addresses(user_profile_id);


### 1. MongoDB (Primary Database)
Sử dụng `mongosh` để chạy các lệnh này. Việc áp dụng `$jsonSchema` validation trực tiếp dưới Database là Best Practice để đảm bảo tính toàn vẹn dữ liệu ngay cả khi có lỗi từ tầng Application.

#### A. Collection: `categories` (Danh mục)
Sử dụng mô hình **Materialized Path** (`path`) để truy vấn cây danh mục (ví dụ: `Ao -> Ao So Mi -> Ao So Mi Nam`) cực nhanh mà không cần đệ quy.

```javascript
db.createCollection("categories", {
  validator: {
    $jsonSchema: {
      bsonType: "object",
      required: ["name", "slug", "path", "is_active"],
      properties: {
        name: { bsonType: "string", description: "Tên danh mục (vd: Áo sơ mi nam)" },
        slug: { bsonType: "string", description: "URL slug duy nhất" },
        parent_id: { bsonType: ["objectId", "null"], description: "ID của danh mục cha" },
        path: { bsonType: "string", description: "Materialized path (vd: /men/shirts/)" },
        image_url: { bsonType: "string", description: "Đường dẫn ảnh trên SeaweedFS" },
        is_active: { bsonType: "bool" },
        sort_order: { bsonType: "int" },
        created_at: { bsonType: "date" },
        updated_at: { bsonType: "date" }
      }
    }
  }
});
// Tạo Index
db.categories.createIndex({ slug: 1 }, { unique: true });
db.categories.createIndex({ path: 1 });
```

#### B. Collection: `brands` (Thương hiệu)
```javascript
db.createCollection("brands", {
  validator: {
    $jsonSchema: {
      bsonType: "object",
      required: ["name", "slug"],
      properties: {
        name: { bsonType: "string" },
        slug: { bsonType: "string" },
        logo_url: { bsonType: "string" },
        description: { bsonType: "string" },
        is_active: { bsonType: "bool" },
        created_at: { bsonType: "date" },
        updated_at: { bsonType: "date" }
      }
    }
  }
});
db.brands.createIndex({ slug: 1 }, { unique: true });
```

#### C. Collection: `products` (Sản phẩm gốc)
Đây là sản phẩm cha. Thuộc tính động (`attributes`) cấu hình danh sách các tùy chọn có thể có (ví dụ: Màu gồm Đỏ, Đen; Size gồm S, M). Nhúng sẵn `brand_name` và `category_names` để hạn chế `$lookup` (Join) khi read.

```javascript
db.createCollection("products", {
  validator: {
    $jsonSchema: {
      bsonType: "object",
      required: ["name", "slug", "category_ids", "base_price", "status"],
      properties: {
        name: { bsonType: "string" },
        slug: { bsonType: "string" },
        brand_id: { bsonType: "objectId" },
        brand_name: { bsonType: "string", description: "Denormalized để đọc nhanh" },
        category_ids: { bsonType: "array", items: { bsonType: "objectId" } },
        description: { bsonType: "string" },
        base_price: { bsonType: "decimal", description: "Giá gốc (hiển thị mặc định)" },
        attributes: { 
          bsonType: "array",
          description: "Các thuộc tính động chung của sản phẩm",
          items: {
            bsonType: "object",
            required: ["name", "options"],
            properties: {
              name: { bsonType: "string", description: "vd: Color" },
              options: { bsonType: "array", items: { bsonType: "string" }, description: "vd: ['Red', 'Blue']" }
            }
          }
        },
        media: {
          bsonType: "array",
          items: {
            bsonType: "object",
            required: ["url", "type"],
            properties: {
              url: { bsonType: "string", description: "Path từ SeaweedFS" },
              type: { enum: ["image", "video"] },
              is_thumbnail: { bsonType: "bool" }
            }
          }
        },
        status: { enum: ["DRAFT", "ACTIVE", "ARCHIVED"], description: "Trạng thái hiển thị" },
        seo: {
          bsonType: "object",
          properties: {
             meta_title: { bsonType: "string" },
             meta_description: { bsonType: "string" }
          }
        },
        created_at: { bsonType: "date" },
        updated_at: { bsonType: "date" }
      }
    }
  }
});
db.products.createIndex({ slug: 1 }, { unique: true });
db.products.createIndex({ brand_id: 1 });
db.products.createIndex({ category_ids: 1 });
db.products.createIndex({ status: 1 });
```

#### D. Collection: `sku_variants` (Biến thể SKU)
Đây là thực thể đại diện cho món hàng vật lý khách hàng mua. Nó liên kết trực tiếp với bảng `InventoryLedger` ở PostgreSQL (Module Inventory) thông qua `sku_code`.

```javascript
db.createCollection("sku_variants", {
  validator: {
    $jsonSchema: {
      bsonType: "object",
      required: ["product_id", "sku_code", "attributes"],
      properties: {
        product_id: { bsonType: "objectId" },
        sku_code: { bsonType: "string", description: "Mã duy nhất toàn hệ thống (vd: SHIRT-RED-L)" },
        barcode: { bsonType: "string", description: "Mã vạch (EAN/UPC) dùng quét tại kho/cửa hàng" },
        attributes: {
          bsonType: "array",
          description: "Định danh biến thể này",
          items: {
            bsonType: "object",
            required: ["key", "value"],
            properties: {
              key: { bsonType: "string", description: "vd: Color" },
              value: { bsonType: "string", description: "vd: Red" }
            }
          }
        },
        price_override: { bsonType: "decimal", description: "Giá riêng của SKU (nếu khác base_price của Product)" },
        media: {
           bsonType: "array", 
           items: { bsonType: "string" },
           description: "Ảnh riêng cho biến thể này (vd: Áo màu đỏ thì lưu ảnh màu đỏ)"
        },
        is_active: { bsonType: "bool" },
        created_at: { bsonType: "date" },
        updated_at: { bsonType: "date" }
      }
    }
  }
});
db.sku_variants.createIndex({ sku_code: 1 }, { unique: true });
db.sku_variants.createIndex({ product_id: 1 });
```

---

### 2. Elasticsearch (Search Model)
Document trong Elasticsearch phải được cấu trúc theo dạng "phẳng" (Flattened) và nhúng toàn bộ data từ Product + SKU_Variant. Khi người dùng lọc thuộc tính trên Storefront, API gọi thẳng vào ES chứ không đụng vào MongoDB.

Chạy lệnh `PUT` qua Kibana hoặc REST API:

```json
PUT /product_catalog_index
{
  "settings": {
    "number_of_shards": 3,
    "number_of_replicas": 1,
    "analysis": {
      "analyzer": {
        "vietnamese_custom": {
          "type": "custom",
          "tokenizer": "standard",
          "filter": ["lowercase", "asciifolding"]
        }
      }
    }
  },
  "mappings": {
    "properties": {
      "product_id": { "type": "keyword" },
      "name": { 
        "type": "text", 
        "analyzer": "vietnamese_custom",
        "fields": { "raw": { "type": "keyword" } }
      },
      "slug": { "type": "keyword" },
      "brand": {
        "properties": {
          "id": { "type": "keyword" },
          "name": { "type": "keyword" }
        }
      },
      "categories": {
        "type": "nested",
        "properties": {
          "id": { "type": "keyword" },
          "name": { "type": "keyword" },
          "path": { "type": "keyword" }
        }
      },
      "price": { "type": "double" },
      "status": { "type": "keyword" },
      "variants": {
        "type": "nested",
        "properties": {
          "sku_code": { "type": "keyword" },
          "attributes": {
            "type": "nested",
            "properties": {
              "key": { "type": "keyword" },
              "value": { "type": "keyword" }
            }
          }
        }
      },
      "created_at": { "type": "date" }
    }
  }
}
```

---

### 3. SeaweedFS (Product Media)
SeaweedFS là một distributed object storage cực nhanh cho các file nhỏ như hình ảnh sản phẩm. Vì nó tương thích hoàn toàn với S3 API, cấu trúc của nó không phải là bảng, mà là các **Bucket**.

Bạn sử dụng AWS CLI (đã config endpoint trỏ về SeaweedFS S3 port, thường là `8333`):

```bash
# Tạo bucket chứa toàn bộ hình ảnh, video của Catalog
aws --endpoint-url http://localhost:8333 s3 mb s3://fashion-catalog-media

# Cấu hình chính sách cho phép đọc công khai (Public Read) để Storefront hiển thị ảnh
aws --endpoint-url http://localhost:8333 s3api put-bucket-policy \
    --bucket fashion-catalog-media \
    --policy '{
        "Version": "2012-10-17",
        "Statement": [
            {
                "Sid": "PublicReadGetObject",
                "Effect": "Allow",
                "Principal": "*",
                "Action": "s3:GetObject",
                "Resource": "arn:aws:s3:::fashion-catalog-media/*"
            }
        ]
    }'
```




-- Kích hoạt extension UUID (nếu chưa có)
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- ==========================================
-- A. WAREHOUSE & STOCK LOCATION (Cấu hình kho)
-- ==========================================
CREATE TABLE warehouses (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    code VARCHAR(50) UNIQUE NOT NULL, -- Vd: WH-HCM-01
    name VARCHAR(255) NOT NULL,
    address TEXT NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE stock_locations (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    warehouse_id UUID NOT NULL REFERENCES warehouses(id) ON DELETE CASCADE,
    zone VARCHAR(50),  -- Khu vực (Vd: Zone A)
    aisle VARCHAR(50), -- Lối đi
    rack VARCHAR(50),  -- Kệ
    shelf VARCHAR(50), -- Tầng
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(warehouse_id, zone, aisle, rack, shelf)
);

-- ==========================================
-- B. INVENTORY SUMMARIES (Snapshot Tồn kho hiện tại)
-- ==========================================
-- Bảng này để query nhanh số lượng tồn kho theo thời gian thực tại 1 kho
CREATE TABLE inventory_summaries (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    warehouse_id UUID NOT NULL REFERENCES warehouses(id),
    sku_code VARCHAR(100) NOT NULL, -- Map với sku_code bên MongoDB
    on_hand INT NOT NULL DEFAULT 0, -- Tồn kho vật lý thực tế
    reserved INT NOT NULL DEFAULT 0, -- Số lượng đang bị giữ (chưa thanh toán)
    available INT GENERATED ALWAYS AS (on_hand - reserved) STORED, -- Tồn kho có thể bán
    version INT NOT NULL DEFAULT 0, -- Hibernate @Version (Optimistic Locking)
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(warehouse_id, sku_code)
);
-- Index hỗ trợ tìm kiếm theo SKU nhanh chóng
CREATE INDEX idx_inventory_sku ON inventory_summaries(sku_code);

-- ==========================================
-- C. INVENTORY LEDGER (Sổ cái kiểm toán)
-- ==========================================
-- Bảng Append-Only (Chỉ thêm mới), cấm UPDATE/DELETE để phục vụ Audit kế toán
CREATE TABLE inventory_ledger (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    warehouse_id UUID NOT NULL REFERENCES warehouses(id),
    sku_code VARCHAR(100) NOT NULL,
    transaction_type VARCHAR(50) NOT NULL, -- Vd: STOCK_IN, STOCK_OUT, RESERVE, RELEASE
    quantity_change INT NOT NULL, -- Có thể âm hoặc dương
    reference_id VARCHAR(100), -- ID của Order, PO, hoặc phiếu nhập kho
    note TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- ==========================================
-- D. STOCK RESERVATION (Giữ hàng tạm thời)
-- ==========================================
CREATE TABLE stock_reservations (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    order_id VARCHAR(100) NOT NULL,
    sku_code VARCHAR(100) NOT NULL,
    warehouse_id UUID NOT NULL REFERENCES warehouses(id),
    quantity INT NOT NULL CHECK (quantity > 0),
    status VARCHAR(50) NOT NULL DEFAULT 'RESERVED', -- RESERVED, CONFIRMED, CANCELLED, EXPIRED
    expires_at TIMESTAMP WITH TIME ZONE NOT NULL, -- Thời gian hết hạn giữ hàng (Vd: +15 phút)
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_reservation_order ON stock_reservations(order_id);
-- Index hỗ trợ Cronjob/Kafka delay queue quét các reservation hết hạn
CREATE INDEX idx_reservation_expires ON stock_reservations(expires_at) WHERE status = 'RESERVED';

-- ==========================================
-- E. FLASH SALE CAMPAIGN (Quản lý chiến dịch)
-- ==========================================
CREATE TABLE flash_sale_campaigns (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(255) NOT NULL,
    start_time TIMESTAMP WITH TIME ZONE NOT NULL,
    end_time TIMESTAMP WITH TIME ZONE NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'DRAFT', -- DRAFT, PUBLISHED, ACTIVE, ENDED
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CHECK (end_time > start_time)
);

CREATE TABLE flash_sale_items (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    campaign_id UUID NOT NULL REFERENCES flash_sale_campaigns(id) ON DELETE CASCADE,
    sku_code VARCHAR(100) NOT NULL,
    promotional_price DECIMAL(15, 2) NOT NULL,
    total_quota INT NOT NULL CHECK (total_quota > 0), -- Tổng số lượng mang ra Flash Sale
    purchase_limit_per_user INT NOT NULL DEFAULT 1, -- Giới hạn mua mỗi user
    UNIQUE(campaign_id, sku_code)
);

-- ==========================================
-- F. OUTBOX EVENT (Hỗ trợ Saga/Kafka)
-- ==========================================
CREATE TABLE outbox_events_inventory (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    aggregate_type VARCHAR(100) NOT NULL, -- Vd: 'INVENTORY'
    aggregate_id VARCHAR(100) NOT NULL, -- ID của Inventory Summary hoặc Ledger
    type VARCHAR(100) NOT NULL, -- Vd: 'StockReservedEvent', 'StockReleasedEvent'
    payload JSONB NOT NULL, -- Chứa chi tiết event để Kafka gửi đi
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING', -- PENDING, PUBLISHED
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    processed_at TIMESTAMP WITH TIME ZONE
);
CREATE INDEX idx_outbox_status ON outbox_events_inventory(status) WHERE status = 'PENDING';



-- Kích hoạt extension UUID
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- ==========================================
-- A. VOUCHER & PROMOTION (Khuyến mãi)
-- ==========================================
CREATE TABLE vouchers (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    code VARCHAR(50) UNIQUE NOT NULL, -- Vd: FREESHIP, TET2024
    discount_type VARCHAR(20) NOT NULL, -- PERCENTAGE, FIXED_AMOUNT
    discount_value DECIMAL(15, 2) NOT NULL, -- % hoặc số tiền cụ thể
    min_order_value DECIMAL(15, 2) DEFAULT 0, -- Giá trị đơn hàng tối thiểu
    max_discount_amount DECIMAL(15, 2), -- Giảm tối đa bao nhiêu tiền (áp dụng cho PERCENTAGE)
    total_quantity INT NOT NULL CHECK (total_quantity >= 0), -- Tổng số lượng mã
    used_quantity INT NOT NULL DEFAULT 0, -- Số lượng đã dùng
    start_time TIMESTAMP WITH TIME ZONE NOT NULL,
    end_time TIMESTAMP WITH TIME ZONE NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE', -- ACTIVE, INACTIVE, EXPIRED
    version INT NOT NULL DEFAULT 0, -- Hibernate @Version (Optimistic Locking chống dùng lố voucher)
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CHECK (end_time > start_time)
);
CREATE INDEX idx_voucher_code ON vouchers(code);

CREATE TABLE user_voucher_wallets (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL, -- ID từ Keycloak
    voucher_id UUID NOT NULL REFERENCES vouchers(id),
    status VARCHAR(20) NOT NULL DEFAULT 'COLLECTED', -- COLLECTED, USED, EXPIRED
    used_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, voucher_id) -- Mỗi user chỉ thu thập 1 loại voucher 1 lần
);

-- ==========================================
-- B. ORDER (Đơn hàng gốc)
-- ==========================================
CREATE TABLE orders (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL,
    order_code VARCHAR(50) UNIQUE NOT NULL, -- Mã dễ đọc cho KH (Vd: ORD-20240315-XYZ)
    
    -- Số tiền
    subtotal_amount DECIMAL(15, 2) NOT NULL CHECK (subtotal_amount >= 0), -- Tổng tiền hàng
    shipping_fee DECIMAL(15, 2) NOT NULL DEFAULT 0,
    discount_amount DECIMAL(15, 2) NOT NULL DEFAULT 0,
    final_amount DECIMAL(15, 2) NOT NULL CHECK (final_amount >= 0), -- Số tiền thực chốt phải trả
    
    -- Trạng thái
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING', 
    -- PENDING (Chờ giữ hàng), RESERVED (Đã giữ kho, chờ thanh toán), 
    -- PAID (Đã thanh toán), PROCESSING, SHIPPING, COMPLETED, CANCELLED
    
    -- Thông tin giao hàng (Lưu dạng Snapshot JSONB hoặc tách cột để không phụ thuộc bảng UserAddress)
    shipping_info JSONB NOT NULL, 
    -- Ví dụ: {"receiver_name": "Quang", "phone": "09xx", "address": "Quận 1, HCM"}
    
    note TEXT,
    version INT NOT NULL DEFAULT 0, -- Optimistic Locking
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
    PRIMARY KEY (id, created_at) -- Bắt buộc phải có partition key trong Primary Key
)PARTITION BY RANGE (created_at);

CREATE INDEX idx_orders_user_id ON orders(user_id);
CREATE INDEX idx_orders_status ON orders(status);
-- Tạo các phân vùng vật lý (Ví dụ: chia theo từng năm hoặc từng quý)
CREATE TABLE orders_2024 PARTITION OF orders FOR VALUES FROM ('2024-01-01') TO ('2025-01-01');
CREATE TABLE orders_2025 PARTITION OF orders FOR VALUES FROM ('2025-01-01') TO ('2026-01-01');
CREATE TABLE order_items (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    product_id VARCHAR(24) NOT NULL,
   order_id UUID NOT NULL,
    sku_code VARCHAR(100) NOT NULL,
    
    -- BẮT BUỘC: Lưu bản chụp (Snapshot) thông tin sản phẩm
    product_name VARCHAR(255) NOT NULL, 
    variant_attributes JSONB, -- Vd: {"Color": "Red", "Size": "L"}
    unit_price DECIMAL(15, 2) NOT NULL, -- Giá tại thời điểm mua
    quantity INT NOT NULL CHECK (quantity > 0),
    subtotal DECIMAL(15, 2) GENERATED ALWAYS AS (unit_price * quantity) STORED,
    
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_order_items_order_id ON order_items(order_id);
-- ==========================================
-- C. PAYMENT TRANSACTION (Giải quyết vấn đề #8 - Idempotency)
-- ==========================================
CREATE TABLE payment_transactions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    order_id UUID NOT NULL,
    provider VARCHAR(50) NOT NULL, -- VNPAY, MOMO, STRIPE, COD
    provider_transaction_id VARCHAR(100), -- Mã giao dịch từ đối tác trả về (Webhook)
    
    amount DECIMAL(15, 2) NOT NULL,
    currency VARCHAR(10) NOT NULL DEFAULT 'VND',
    
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING', -- PENDING, SUCCESS, FAILED, REFUNDED
    
    -- Cột sống còn cho Idempotency: API Gateway hoặc Frontend tạo ra một UUID gửi xuống
    idempotency_key VARCHAR(100) UNIQUE NOT NULL, 
    
    error_message TEXT, -- Lưu lỗi nếu thanh toán thất bại
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_payment_order ON payment_transactions(order_id);

-- ==========================================
-- D. SHIPMENT / DELIVERY (Vận đơn)
-- ==========================================
CREATE TABLE shipments (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    order_id UUID NOT NULL,
    tracking_code VARCHAR(100) UNIQUE, -- Mã vận đơn của GHN/GHTK
    provider VARCHAR(50) NOT NULL, -- GHN, GHTK, NINJAVAN
    
    status VARCHAR(30) NOT NULL DEFAULT 'PREPARING', 
    -- PREPARING, PICKED_UP, IN_TRANSIT, DELIVERED, RETURNED
    
    estimated_delivery_date DATE,
    actual_delivery_date TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- ==========================================
-- E. INFRASTRUCTURE: SAGA STATE & OUTBOX
-- ==========================================
-- Dùng để track trạng thái giao dịch phân tán (Distributed Transaction)
CREATE TABLE saga_states (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    order_id UUID UNIQUE NOT NULL,
    current_step VARCHAR(50) NOT NULL, -- Vd: RESERVE_INVENTORY, PROCESS_PAYMENT
    status VARCHAR(30) NOT NULL, -- STARTED, COMPLETED, COMPENSATING, ABORTED
    payload JSONB, -- Lưu context để có thể tự động rollback (Compensate)
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Bảng Outbox pattern đảm bảo không mất message khi hệ thống sập giữa chừng
CREATE TABLE outbox_events_order (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    
    -- Định danh thực thể sinh ra event
    aggregate_type VARCHAR(100) NOT NULL, -- Vd: 'INVENTORY', 'ORDER'
    aggregate_id VARCHAR(100) NOT NULL,   -- ID của Inventory Summary hoặc Order
    
    -- Phân loại Event
    type VARCHAR(100) NOT NULL,           -- Vd: 'OrderCreatedEvent', 'StockReservedEvent'
    
    -- Dữ liệu thực tế bắn lên Kafka
    payload JSONB NOT NULL,               -- Chứa chi tiết event
    
    -- [MỚI BỔ SUNG] - Bắt buộc cho Production
    trace_id VARCHAR(100),                -- Trace ID từ OpenTelemetry/Micrometer để truy vết log xuyên module
    retry_count INT NOT NULL DEFAULT 0,   -- Đếm số lần worker thử gửi Kafka nhưng thất bại
    error_message TEXT,                   -- Lưu nguyên nhân lỗi (nếu có) để Admin dễ debug
    
    -- Quản lý vòng đời
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING', 
    -- Các trạng thái chuẩn: PENDING (Chờ gửi), PUBLISHED (Đã gửi), FAILED (Lỗi vĩnh viễn sau X lần retry)
    
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    processed_at TIMESTAMP WITH TIME ZONE
);

-- ==========================================
-- INDEXES TỐI ƯU HÓA (CỰC KỲ QUAN TRỌNG)
-- ==========================================

-- 1. Partial Index: Worker (Spring Scheduler) chỉ tìm các dòng PENDING để bắn Kafka.
-- Index này bỏ qua hàng triệu dòng PUBLISHED, giúp câu lệnh SELECT cực nhanh.
CREATE INDEX idx_outbox_pending_processing 
ON outbox_events(status, created_at) 
WHERE status = 'PENDING';

-- 2. Index hỗ trợ truy vấn xem 1 Order/Inventory cụ thể đã bắn những event nào
CREATE INDEX idx_outbox_aggregate 
ON outbox_events(aggregate_type, aggregate_id);
CREATE INDEX idx_outbox_status ON outbox_events(status) WHERE status = 'PENDING';




-- 1. Kích hoạt các extension cần thiết
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS vector; -- Bắt buộc phải cài đặt PGVector extension

-- ==========================================
-- A. VECTOR KNOWLEDGE BASE (Dữ liệu cho RAG)
-- ==========================================

-- Bảng lưu trữ Vector của Sản phẩm (Dùng cho Visual Search & Semantic Search)
CREATE TABLE product_embeddings (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    product_id VARCHAR(100) NOT NULL, -- Map với MongoDB
    sku_code VARCHAR(100),
    
    -- Dữ liệu gốc dạng Text (được bóc tách từ tên, mô tả, thuộc tính, hoặc ảnh)
    content TEXT NOT NULL, 
    
    -- Vector lưu trữ ngữ nghĩa (768 với Gemini, 1536 với OpenAI)
    embedding VECTOR(1536) NOT NULL, 
    
    -- Metadata cực kỳ quan trọng dùng để Filter metadata trong Spring AI
    -- Ví dụ: {"category": "shirt", "color": "red", "price": 150000}
    metadata JSONB NOT NULL DEFAULT '{}'::jsonb, 
    
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- BEST PRACTICE: Tạo Index HNSW (Hierarchical Navigable Small World) 
-- Đây là thuật toán xịn nhất hiện tại cho PGVector, tốc độ tìm kiếm cực nhanh
CREATE INDEX idx_product_embedding_hnsw 
ON product_embeddings 
USING hnsw (embedding vector_cosine_ops);

-- Bảng lưu trữ các bài viết Blog, Hướng dẫn phối đồ, Quy định đổi trả (Text Chunks)
CREATE TABLE style_guide_chunks (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    document_id VARCHAR(100) NOT NULL, -- ID của bài viết gốc
    chunk_index INT NOT NULL, -- Thứ tự của đoạn text trong bài viết
    content TEXT NOT NULL, -- Nội dung đoạn text (VD: "Quần tây đen rất hợp với sơ mi trắng...")
    embedding VECTOR(1536) NOT NULL,
    metadata JSONB NOT NULL DEFAULT '{}'::jsonb, -- {"author": "Stylist A", "tags": ["công sở"]}
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_style_guide_hnsw 
ON style_guide_chunks 
USING hnsw (embedding vector_cosine_ops);

-- ==========================================
-- B. AI PERSONA & GUARDRAILS (Kiểm soát AI)
-- ==========================================

-- Bảng thiết lập "Tính cách" và System Prompt cho AI
CREATE TABLE persona_configs (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(100) UNIQUE NOT NULL, -- VD: "GenZ_Stylist", "Office_Professional"
    
    -- Dòng lệnh định hình AI. 
    -- VD: "Bạn là chuyên gia thời trang năng động. Luôn xưng 'mình' và gọi khách là 'bạn'..."
    system_prompt TEXT NOT NULL, 
    
    tone VARCHAR(50), -- FRIENDLY, PROFESSIONAL, LUXURY
    is_active BOOLEAN DEFAULT FALSE, -- Chỉ có 1 Persona active tại 1 thời điểm
    created_by VARCHAR(100), -- Admin ID
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Bảng Guardrails: Thiết lập ranh giới để chặn AI trả lời tào lao (Vấn đề #6)
CREATE TABLE ai_prompt_guardrails (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    topic_name VARCHAR(100) NOT NULL, -- VD: "Politics", "Competitor_Brands", "Coding"
    
    -- BLOCK (Chặn luôn), REDIRECT (Chuyển hướng về thời trang)
    action_type VARCHAR(50) NOT NULL DEFAULT 'REDIRECT', 
    
    -- Danh sách từ khóa mồi để bắt filter (Regex hoặc mảng text)
    trigger_keywords JSONB NOT NULL, -- VD: ["chính trị", "tôn giáo", "Shopee", "Lazada"]
    
    -- Câu trả lời đóng gói sẵn nếu vi phạm
    -- VD: "Dạ, em chỉ là Trợ lý Thời trang nên không rành vấn đề này ạ. Mình xem thêm áo thun nhé?"
    canned_response TEXT NOT NULL, 
    
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);



Chào Quang, 

Thiết kế ban đầu của bạn cho Neo4j đã đi đúng hướng cơ bản của một Knowledge Graph. Tuy nhiên, để đạt mức **100% Best Practice cho hệ thống AI Recommendation (Gợi ý) và AI Stylist (Tư vấn phối đồ)** trong Fashion E-commerce, thiết kế đó **chưa đủ**.

**Lý do (Vấn đề của thiết kế cũ):**
Trong Neo4j (Graph DB), nếu bạn lưu `category`, `color`, `brand` dưới dạng **Properties (Thuộc tính)** của Node `Product`, thuật toán truy vấn mạng lưới (Graph Traversal) sẽ chạy rất chậm khi AI cần trả lời câu hỏi: *"Tìm tất cả các áo sơ mi (Category) màu trắng (Color) của hãng Zara (Brand) thường được mặc chung với quần tây đen"*. 

**Best Practice trong Neo4j:** Những thực thể dùng để "nhóm" (Group) hoặc "lọc" (Filter) phải được tách ra thành **Các Node riêng biệt**. Ngoài ra, cần bổ sung các hành vi (View, Cart, Wishlist) vì AI cần dữ liệu này để học sở thích người dùng (Collaborative Filtering).

Dưới đây là thiết kế **hoàn chỉnh 100% (Nodes, Relationships, Properties)** và script Cypher chuẩn cho Neo4j.

---

### 1. Cấu trúc Các Node (Thực thể) & Properties

Chúng ta sẽ mở rộng hệ sinh thái Node để con AI có thể "suy luận" sâu hơn.

| Tên Node (Label) | Ý nghĩa định danh | Các Properties (Thuộc tính) 100% |
| :--- | :--- | :--- |
| **`Customer`** | Khách hàng | `user_id` (String - PK, map với PostgreSQL/Keycloak), `gender` (String), `age_group` (String), `location` (String), `premium_member` (Boolean), `joined_at` (Datetime) |
| **`Product`** | Sản phẩm vật lý | `product_id` (String - PK, map với MongoDB), `name` (String), `price_segment` (String: Giá rẻ/Trung cấp/Cao cấp) |
| **`Category`** | Danh mục | `category_id` (String - PK), `name` (String: Áo thun, Quần tây...), `level` (Int: 1 là gốc, 2 là nhánh con) |
| **`Brand`** | Thương hiệu | `brand_id` (String - PK), `name` (String) |
| **`Color`** | Màu sắc | `name` (String - PK: Trắng, Đen, Đỏ...) |
| **`Material`** | Chất liệu | `name` (String - PK: Cotton, Linen, Denim...) |
| **`Style`** | Phong cách/Dịp | `style_id` (String - PK), `name` (String: Công sở, Đi tiệc, Dạo phố), `season` (String: Xuân, Hè...) |

### 2. Cấu trúc Relationships (Mối quan hệ) & Properties

Xương sống của hệ thống gợi ý nằm ở các thuộc tính (Trọng số/Weight) trên các mối quan hệ.

| Từ Node | Tên Mối Quan Hệ | Đến Node | Các Properties trên Relationship | Ý nghĩa cho AI RAG |
| :--- | :--- | :--- | :--- | :--- |
| `Customer` | **`VIEWED`** | `Product` | `viewed_at` (Datetime), `times` (Int: số lần xem), `duration_sec` (Int) | Đo lường độ quan tâm nhẹ |
| `Customer` | **`ADDED_TO_CART`** | `Product` | `added_at` (Datetime) | Đo lường ý định mua cao |
| `Customer` | **`WISHLISTED`** | `Product` | `wishlisted_at` (Datetime) | Sở thích dài hạn |
| `Customer` | **`BOUGHT`** | `Product` | `purchased_at` (Datetime), `rating` (Int: 1-5), `quantity` (Int) | Khẳng định gu thời trang thực tế |
| `Product` | **`MATCHES_WITH`** | `Product` | `confidence_score` (Float: 0.1-1.0), `source` (String: 'AI_Generated' hoặc 'Human_Stylist') | Gợi ý Outfit (Mix & Match) |
| `Product` | **`SIMILAR_TO`** | `Product` | `similarity_score` (Float: 0.1-1.0) | Gợi ý sản phẩm thay thế (Upsell/Cross-sell) |
| `Product` | **`BELONGS_TO`** | `Category` | Không cần property | Giúp AI lọc nhóm đồ |
| `Product` | **`MADE_BY`** | `Brand` | Không cần property | Giúp AI biết hãng |
| `Product` | **`HAS_COLOR`** | `Color` | Không cần property | AI dễ dàng tìm đồ cùng màu |
| `Product` | **`MADE_OF`** | `Material` | `percentage` (Float: vd 100% cotton) | Tư vấn chất liệu theo mùa |
| `Product` | **`SUITABLE_FOR`** | `Style` | `weight` (Float: Độ phù hợp 0.1-1.0) | Trả lời câu "Tôi muốn đi tiệc" |

---

### 3. Script Khởi tạo Cypher (100% Best Practice)

Chạy tuần tự các script sau trên Neo4j để khởi tạo toàn bộ Schema và Data mẫu.

#### Bước 3.1: Tạo Constraints và Indexes (Bắt buộc chạy đầu tiên)
Tạo index giúp tốc độ truy vấn Node từ $O(n)$ giảm xuống mức mili-giây.

```cypher
// Xóa index cũ nếu có (bỏ qua nếu database trắng)
// Ràng buộc duy nhất (Unique Constraints) - Đóng vai trò như Primary Key
CREATE CONSTRAINT customer_id_unique IF NOT EXISTS FOR (c:Customer) REQUIRE c.user_id IS UNIQUE;
CREATE CONSTRAINT product_id_unique IF NOT EXISTS FOR (p:Product) REQUIRE p.product_id IS UNIQUE;
CREATE CONSTRAINT category_id_unique IF NOT EXISTS FOR (cat:Category) REQUIRE cat.category_id IS UNIQUE;
CREATE CONSTRAINT brand_id_unique IF NOT EXISTS FOR (b:Brand) REQUIRE b.brand_id IS UNIQUE;
CREATE CONSTRAINT color_name_unique IF NOT EXISTS FOR (col:Color) REQUIRE col.name IS UNIQUE;
CREATE CONSTRAINT material_name_unique IF NOT EXISTS FOR (m:Material) REQUIRE m.name IS UNIQUE;
CREATE CONSTRAINT style_id_unique IF NOT EXISTS FOR (s:Style) REQUIRE s.style_id IS UNIQUE;

// Indexes cho các luồng tìm kiếm phụ trợ
CREATE INDEX product_price_seg_idx IF NOT EXISTS FOR (p:Product) ON (p.price_segment);
```

#### Bước 3.2: Cập nhật dữ liệu Nodes (Sử dụng `MERGE` để tránh duplicate)
Trong thực tế, Kafka Consumer (hoặc Debezium) sẽ trigger các lệnh `MERGE` này mỗi khi có Product mới tạo bên MongoDB hoặc User mới đăng ký bên Keycloak.

```cypher
// 1. Tạo Khách hàng
MERGE (c:Customer {user_id: "user-123"})
  ON CREATE SET c.gender = "Female", c.age_group = "18-24", c.location = "HCM", c.premium_member = true, c.joined_at = datetime();

// 2. Tạo các Node Thuộc tính (Metadata Nodes)
MERGE (cat1:Category {category_id: "CAT-001"}) ON CREATE SET cat1.name = "Áo Sơ Mi", cat1.level = 2;
MERGE (cat2:Category {category_id: "CAT-002"}) ON CREATE SET cat2.name = "Quần Tây", cat2.level = 2;
MERGE (b1:Brand {brand_id: "BRD-ZARA"}) ON CREATE SET b1.name = "Zara";
MERGE (col1:Color {name: "Trắng"});
MERGE (col2:Color {name: "Đen"});
MERGE (mat1:Material {name: "Cotton"});
MERGE (sty1:Style {style_id: "STY-OFFICE"}) ON CREATE SET sty1.name = "Công sở", sty1.season = "All";
MERGE (sty2:Style {style_id: "STY-PARTY"}) ON CREATE SET sty2.name = "Đi tiệc", sty2.season = "All";

// 3. Tạo Sản phẩm
MERGE (p1:Product {product_id: "PROD-SHIRT-01"})
  ON CREATE SET p1.name = "Sơ mi lụa tơ tằm", p1.price_segment = "Premium";
MERGE (p2:Product {product_id: "PROD-PANTS-02"})
  ON CREATE SET p2.name = "Quần âu ống suông", p2.price_segment = "Mid-range";
```

#### Bước 3.3: Nối các Relationship (Xây dựng Knowledge Graph)

```cypher
// 1. Gắn Thuộc tính cho Sản Phẩm (Product Knowledge)
MATCH (p1:Product {product_id: "PROD-SHIRT-01"}), (cat1:Category {category_id: "CAT-001"}), 
      (b1:Brand {brand_id: "BRD-ZARA"}), (col1:Color {name: "Trắng"}), 
      (mat1:Material {name: "Cotton"}), (sty1:Style {style_id: "STY-OFFICE"}), (sty2:Style {style_id: "STY-PARTY"})
MERGE (p1)-[:BELONGS_TO]->(cat1)
MERGE (p1)-[:MADE_BY]->(b1)
MERGE (p1)-[:HAS_COLOR]->(col1)
MERGE (p1)-[mo:MADE_OF]->(mat1) ON CREATE SET mo.percentage = 100.0
MERGE (p1)-[sf1:SUITABLE_FOR]->(sty1) ON CREATE SET sf1.weight = 0.9
MERGE (p1)-[sf2:SUITABLE_FOR]->(sty2) ON CREATE SET sf2.weight = 0.6;

MATCH (p2:Product {product_id: "PROD-PANTS-02"}), (cat2:Category {category_id: "CAT-002"}), 
      (col2:Color {name: "Đen"}), (sty1:Style {style_id: "STY-OFFICE"})
MERGE (p2)-[:BELONGS_TO]->(cat2)
MERGE (p2)-[:HAS_COLOR]->(col2)
MERGE (p2)-[sf:SUITABLE_FOR]->(sty1) ON CREATE SET sf.weight = 0.95;

// 2. Định nghĩa Outfit Mix & Match (AI Stylist Core)
MATCH (p1:Product {product_id: "PROD-SHIRT-01"}), (p2:Product {product_id: "PROD-PANTS-02"})
MERGE (p1)-[mw:MATCHES_WITH]->(p2)
  ON CREATE SET mw.confidence_score = 0.95, mw.source = "Human_Stylist";

// 3. Ghi nhận Hành vi Khách hàng (User Behavior / Collaborative Filtering)
MATCH (c:Customer {user_id: "user-123"}), (p1:Product {product_id: "PROD-SHIRT-01"}), (p2:Product {product_id: "PROD-PANTS-02"})
MERGE (c)-[v:VIEWED]->(p1)
  ON CREATE SET v.viewed_at = datetime(), v.times = 1, v.duration_sec = 45
  ON MATCH SET v.times = v.times + 1, v.viewed_at = datetime()
MERGE (c)-[w:WISHLISTED]->(p2)
  ON CREATE SET w.wishlisted_at = datetime()
MERGE (c)-[b:BOUGHT]->(p1)
  ON CREATE SET b.purchased_at = datetime(), b.rating = 5, b.quantity = 2;
```

---
1. Khởi tạo Keyspace (Tương đương Database trong SQL)
Nên sử dụng NetworkTopologyStrategy cho môi trường Production để nhân bản dữ liệu ra nhiều node/data center nhằm đảm bảo tính High Availability (sập 1 node không mất data).

SQL
-- Chạy trên cqlsh
CREATE KEYSPACE IF NOT EXISTS fashion_ecommerce_audit 
WITH replication = {'class': 'NetworkTopologyStrategy', 'datacenter1': '3'} 
AND durable_writes = true;

USE fashion_ecommerce_audit;
2. Bảng order_audit_logs (Lịch sử thay đổi đơn hàng)
Bài toán: Admin hoặc khách hàng muốn xem "Đơn hàng ORD-123 đã trải qua những trạng thái nào, ai là người hủy đơn vào lúc mấy giờ?".
Best Practice: Gom cụm (Partition) theo order_id. Sắp xếp (Clustering) theo event_time giảm dần để lấy log mới nhất lên đầu với tốc độ mili-giây.

SQL
CREATE TABLE order_audit_logs (
    order_id UUID,                -- Map với bảng orders bên PostgreSQL
    event_time TIMESTAMP,         -- Thời gian xảy ra thay đổi
    event_id TIMEUUID,            -- Đảm bảo unique tuyệt đối nếu có 2 event cùng 1 mili-giây
    action_type TEXT,             -- CREATE, UPDATE, CANCEL, REFUND
    changed_by TEXT,              -- ID của User, Admin, hoặc 'SYSTEM' (nếu tự động)
    old_status TEXT,              -- Trạng thái trước khi đổi (VD: PENDING)
    new_status TEXT,              -- Trạng thái sau khi đổi (VD: PAID)
    
    -- Envers sẽ tạo ra diff (sự khác biệt) của entity. Tầng Consumer 
    -- sẽ parse thành chuỗi JSON và lưu vào đây. Cassandra không có JSONB, dùng TEXT.
    changes_payload TEXT,         
    
    reason TEXT,                  -- Lý do (VD: "Khách hàng đổi ý", "Lỗi cổng thanh toán")
    
    PRIMARY KEY ((order_id), event_time, event_id)
) WITH CLUSTERING ORDER BY (event_time DESC);
3. Bảng inventory_audit_logs (Lịch sử biến động kho)
Bài toán: Kế toán cần truy vết "Tại sao áo sơ mi đỏ ở kho HCM lại hụt mất 2 cái vào ngày hôm qua?".
Best Practice: Gom cụm (Partition) theo warehouse_id và sku_code.

SQL
CREATE TABLE inventory_audit_logs (
    warehouse_id UUID,
    sku_code TEXT,
    event_time TIMESTAMP,
    event_id TIMEUUID,
    
    transaction_type TEXT,        -- STOCK_IN (Nhập), STOCK_OUT (Xuất), RESERVE (Giữ), RELEASE (Hoàn)
    quantity_change INT,          -- Số lượng thay đổi (+10, -2)
    balance_after INT,            -- Tồn kho sau khi thay đổi (Rất quan trọng để đối soát)
    
    reference_id TEXT,            -- Mã đơn hàng (order_id) hoặc Mã phiếu nhập kho (PO) gây ra thay đổi này
    changed_by TEXT,              -- Ai thực hiện
    note TEXT,                    -- Ghi chú thêm
    
    PRIMARY KEY ((warehouse_id, sku_code), event_time, event_id)
) WITH CLUSTERING ORDER BY (event_time DESC);
4. Bảng flash_sale_realtime_metrics (Lưu vết System Metric thời gian thực)
Mặc dù dữ liệu "đang sống" (vơi kho, số request) được đẩy qua WebSocket/SSE trực tiếp từ Redis xuống Frontend Dashboard để xem live, nhưng nếu Admin muốn "xem lại biểu đồ chịu tải ngày hôm qua", ta cần ghi log Time-series.

Best Practice: Dữ liệu hệ thống bắn liên tục sẽ làm 1 Partition quá to (quá 100MB là Cassandra sẽ chậm). Ta phải dùng kỹ thuật Time Bucketing (chia xô theo thời gian, ví dụ chia theo Ngày-Giờ). Đồng thời cài đặt default_time_to_live (TTL) tự động xóa data sau 30 ngày để tiết kiệm dung lượng.

SQL
CREATE TABLE flash_sale_realtime_metrics (
    campaign_id UUID,
    
    -- Xô thời gian (Ví dụ: '2024-03-21-15' đại diện cho 15h ngày 21/03/2024). 
    -- Giúp giới hạn lượng data trong 1 partition.
    time_bucket TEXT,             
    
    event_time TIMESTAMP,
    sku_code TEXT,
    sold_quantity INT,            -- Số lượng đã bán được chốt tại thời điểm này
    current_rps INT,              -- Số lượng Request Per Second ghi nhận tại thời điểm này
    memory_usage_mb DOUBLE,       -- Lượng RAM server Order đang tiêu thụ
    
    PRIMARY KEY ((campaign_id, time_bucket), event_time)
) WITH CLUSTERING ORDER BY (event_time DESC)
  AND default_time_to_live = 2592000; -- Tự động xóa (TTL) sau 30 ngày (2,592,000 giây)
5. Bảng system_error_logs (Ghi nhận lỗi hệ thống chéo Module)
Module RAG gọi LLM bị timeout, Module Order gọi Payment bị đứt cáp... Kafka sẽ gom hết các Error Event này ném về Cassandra để phục vụ monitor và debug tập trung, thay vì phải chui vào từng console của microservice để mò mẫm.

SQL
CREATE TABLE system_error_logs (
    service_name TEXT,            -- Tên module bị lỗi (VD: 'CATALOG_SERVICE', 'AI_RAG_SERVICE')
    time_bucket TEXT,             -- Tương tự trên, gom theo ngày (VD: '2024-03-21')
    event_time TIMESTAMP,
    error_id TIMEUUID,
    
    error_code TEXT,              -- Mã lỗi (VD: 'ERR_PAYMENT_TIMEOUT')
    severity TEXT,                -- WARNING, ERROR, CRITICAL, FATAL
    stack_trace TEXT,             -- Chi tiết lỗi
    request_payload TEXT,         -- Đầu vào gây ra lỗi
    endpoint TEXT,                -- API endpoint bị lỗi
    
    PRIMARY KEY ((service_name, time_bucket), event_time, error_id)
) WITH CLUSTERING ORDER BY (event_time DESC)
  AND default_time_to_live = 5184000; -- Lưu log lỗi trong 60 ngày










  CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- ==========================================
-- A. NOTIFICATION TEMPLATES (Mẫu tin nhắn)
-- ==========================================
CREATE TABLE notification_templates (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    code VARCHAR(100) UNIQUE NOT NULL, -- VD: ORDER_SUCCESS, FLASH_SALE_START, AI_OUTFIT_SUGGESTION
    channel VARCHAR(20) NOT NULL, -- EMAIL, SMS, PUSH, IN_APP
    
    title_template VARCHAR(255), -- Dùng cho Push/Email/In-App (VD: "Đơn hàng {{order_id}} đã đặt thành công!")
    body_template TEXT NOT NULL, -- Nội dung HTML hoặc Text trơn
    
    -- Danh sách các biến bắt buộc phải truyền vào khi gọi API gửi thông báo
    required_variables JSONB DEFAULT '[]'::jsonb, -- VD: ["user_name", "order_id", "total_amount"]
    
    is_active BOOLEAN DEFAULT TRUE,
    created_by VARCHAR(100), -- Admin ID
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_template_channel ON notification_templates(channel);

-- ==========================================
-- B. USER PREFERENCES (Cấu hình nhận thông báo)
-- ==========================================
-- Phải check bảng này trước khi gửi để tránh dính report Spam
CREATE TABLE user_preferences (
    user_id UUID PRIMARY KEY, -- ID từ Keycloak
    
    email_opt_in BOOLEAN DEFAULT TRUE,
    sms_opt_in BOOLEAN DEFAULT FALSE, -- SMS tốn tiền, thường default false
    push_opt_in BOOLEAN DEFAULT TRUE,
    in_app_opt_in BOOLEAN DEFAULT TRUE,
    
    -- Tính năng Do Not Disturb (DND) - Không làm phiền ban đêm
    dnd_start_time TIME, -- VD: 22:00
    dnd_end_time TIME,   -- VD: 06:00
    
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- ==========================================
-- C. DEVICE TOKENS (Bản gốc của Token thiết bị)
-- ==========================================
-- Dùng để Firebase/APNs biết phải bắn Push Notification vào cái điện thoại nào
CREATE TABLE device_tokens (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL,
    device_id VARCHAR(255) NOT NULL, -- Mã định danh thiết bị vật lý
    device_type VARCHAR(20) NOT NULL, -- IOS, ANDROID, WEB
    
    token TEXT NOT NULL, -- FCM Token hoặc APNs Token
    is_active BOOLEAN DEFAULT TRUE,
    
    last_used_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, device_id)
);
CREATE INDEX idx_device_tokens_user ON device_tokens(user_id) WHERE is_active = TRUE;


3. MongoDB (In-App Notification - "Quả Chuông")
Giao diện "Quả chuông" yêu cầu schema cực kỳ linh hoạt (thông báo AI thì có kèm ảnh outfit, thông báo đơn hàng thì kèm link tracking). RDBMS sẽ phải tạo rất nhiều cột NULL, do đó dùng MongoDB là chuẩn bài.

Sử dụng mongosh:

JavaScript
db.createCollection("in_app_notifications", {
  validator: {
    $jsonSchema: {
      bsonType: "object",
      required: ["user_id", "type", "title", "content", "is_read", "created_at"],
      properties: {
        user_id: { bsonType: "string", description: "Lưu dưới dạng UUID String" },
        type: { 
          enum: ["ORDER", "PROMOTION", "SYSTEM", "AI_STYLIST"],
          description: "Phân loại để hiển thị icon khác nhau trên UI"
        },
        title: { bsonType: "string" },
        content: { bsonType: "string" },
        
        // Metadata linh hoạt (Deep links, ảnh)
        image_url: { bsonType: "string" },
        action_url: { bsonType: "string", description: "Deep link để click vào (VD: app://order/123)" },
        reference_id: { bsonType: "string", description: "ID của Order hoặc Campaign để tra cứu ngược" },
        
        is_read: { bsonType: "bool", description: "Trạng thái đọc" },
        read_at: { bsonType: "date" },
        created_at: { bsonType: "date" },
        expires_at: { bsonType: "date", description: "TTL index để tự xóa thông báo rác" }
      }
    }
  }
});

// Best Practice: Indexes
// 1. Dùng để query danh sách quả chuông cho user, sort mới nhất lên đầu
db.in_app_notifications.createIndex({ user_id: 1, created_at: -1 });

// 2. Tự động xóa (TTL Index) các thông báo đã cũ sau 30 ngày (tính từ expires_at)
db.in_app_notifications.createIndex({ expires_at: 1 }, { expireAfterSeconds: 0 });

// 3. Đếm số lượng thông báo chưa đọc (Unread badge)
db.in_app_notifications.createIndex({ user_id: 1, is_read: 1 });
4. Cassandra (Notification Logs)
Giám sát việc đối tác (SendGrid, Twilio) có gửi tin nhắn thành công hay không, hoặc khách hàng khiếu nại "Tôi chưa nhận được email mã giảm giá". Khối lượng ghi là cực lớn (Write-Heavy).

Sử dụng cqlsh:

SQL
USE fashion_ecommerce_audit;

-- Bảng lưu vết lịch sử gửi tin
CREATE TABLE notification_logs (
    user_id TEXT,                 -- Partition Key chính để tìm log theo user
    time_bucket TEXT,             -- Gom nhóm theo tháng (VD: '2024-03') để tránh Hot Partition
    sent_at TIMESTAMP,            -- Thời gian gửi (Clustering Column để sort)
    log_id TIMEUUID,              -- Unique ID cho sự kiện
    
    channel TEXT,                 -- EMAIL, SMS, PUSH
    template_code TEXT,           -- Mã template đã dùng
    provider TEXT,                -- Đối tác gửi (VD: 'SENDGRID', 'TWILIO', 'FIREBASE')
    provider_message_id TEXT,     -- ID do đối tác trả về để đối soát (Webhook)
    
    recipient_address TEXT,       -- SĐT, Email, hoặc FCM Token thực tế nhận
    status TEXT,                  -- SENT, DELIVERED, FAILED, READ
    error_message TEXT,           -- Lý do lỗi (nếu status = FAILED)
    
    PRIMARY KEY ((user_id, time_bucket), sent_at, log_id)
) WITH CLUSTERING ORDER BY (sent_at DESC)
  AND default_time_to_live = 7776000; -- Tự động xóa log sau 90 ngày (7,776,000 gi