

### PHẦN 1: CÁC ENTITY CẦN VIẾT KAFKA CONSUMER
Consumer là các Worker chạy ngầm, liên tục lắng nghe các Topic trên Kafka (được bắn ra từ bảng `outbox_events_...` của các module khác) để thực thi logic cập nhật Database nội bộ.

#### 1. Module Inventory (Quản lý Kho)
Module này là Consumer "bận rộn" nhất, lắng nghe các sự kiện từ Module Order.
* **Entity bị tác động:** `stock_reservations`, `inventory_summaries`, `inventory_ledger`.
* **Lắng nghe Topic:** `order-events`
* **Xử lý các Event:**
    * `OrderCreatedEvent`: Bỏ qua dữ liệu gốc, chỉ trích xuất danh sách `sku_code` và `quantity`. **Create** một record vào bảng `stock_reservations` (trạng thái RESERVED) và trừ vào cột ảo `available` của bảng `inventory_summaries`.
    * `OrderPaidEvent`: Cập nhật `stock_reservations` thành CONFIRMED. Đồng thời, **Create** dòng mới trong `inventory_ledger` (transaction_type: `STOCK_OUT`) và cập nhật giảm số lượng ở cột `on_hand` trong `inventory_summaries`.
    * `OrderCancelledEvent` / `PaymentFailedEvent`: Cập nhật `stock_reservations` thành CANCELLED. Hoàn trả lại số lượng cho cột `available` trong `inventory_summaries`.

#### 2. Module Order (Đơn hàng)
Đóng vai trò là Saga Orchestrator (hoặc Choreography), Order phải lắng nghe ngược lại kết quả từ Inventory và Payment.
* **Entity bị tác động:** `orders`, `saga_states`, `outbox_events_order`.
* **Lắng nghe Topic:** `inventory-events`, `payment-events`
* **Xử lý các Event:**
    * `StockReservedSuccessEvent` (Từ Inventory): **Update** trạng thái `orders` từ PENDING sang RESERVED. Cập nhật `saga_states` sang bước tiếp theo (PROCESS_PAYMENT).
    * `StockReservedFailedEvent` (Từ Inventory do hết hàng): **Update** `orders` thành CANCELLED. Cập nhật `saga_states` thành COMPENSATING (tiến hành rollback).
    * `PaymentSuccessEvent` (Từ Webhook Payment): **Update** trạng thái `orders` thành PAID.

#### 3. Module AI Stylist & Recommendation (Neo4j GraphDB)
Hệ thống AI không tự sinh ra dữ liệu giao dịch, nó "hút" dữ liệu để học hỏi hành vi.
* **Entity bị tác động:** Relationships `[:ADDED_TO_CART]`, `[:BOUGHT]`.
* **Lắng nghe Topic:** `order-events`
* **Xử lý các Event:**
    * `CartUpdatedEvent`: Chạy lệnh Cypher MERGE quan hệ `[:ADDED_TO_CART]` giữa `Customer` và `Product`.
    * `OrderPaidEvent`: Chạy lệnh Cypher MERGE quan hệ `[:BOUGHT]` với các properties như `purchased_at`, `quantity`.

#### 4. Module Notification (Thông báo)
Hoàn toàn thụ động, lắng nghe để kích hoạt luồng bắn tin nhắn/email.
* **Entity bị tác động:** `in_app_notifications` (MongoDB), `notification_logs` (Cassandra).
* **Lắng nghe Topic:** Tất cả các Topic (`order-events`, `inventory-events`, `promotion-events`).
* **Xử lý các Event:**
    * Khi có bất kỳ Event nào như `OrderCreated`, `OrderShipped`, Consumer sẽ check bảng `user_preferences` (PostgreSQL), sau đó **Create** một quả chuông trong MongoDB và **Create** log vào Cassandra khi đã gọi API SendGrid/Firebase thành công.

#### 5. Module Audit (Cassandra)
Lắng nghe mọi thứ để phục vụ truy vết kế toán và hệ thống.
* **Entity bị tác động:** `order_audit_logs`, `inventory_audit_logs`.
* **Lắng nghe Topic:** Tất cả Outbox Topic.
* **Xử lý:** Bóc tách payload JSON từ Kafka để **Create** các dòng log time-series xuống Cassandra.

---

### PHẦN 2: CRUD MODULE ORDER ẢNH HƯỞNG CHÉO RA SAO? (RIPPLE EFFECTS)

Khi một thao tác CRUD xảy ra tại RDBMS của Order (PostgreSQL), nó sẽ tạo ra chuỗi phản ứng dây chuyền (thông qua Outbox Pattern -> Kafka -> Consumer của các Module khác) như sau: 

#### Kịch bản 1: Khách hàng bấm "ĐẶT HÀNG" (Create Order)
* **Nội bộ (Synchronous - chung 1 Transaction DB):**
    * **CREATE** `orders`, `order_items`.
    * **UPDATE** `vouchers`, `user_voucher_wallets` (Khóa/trừ mã giảm giá).
    * **CREATE** `saga_states` (Bắt đầu theo dõi).
    * **CREATE** `outbox_events_order` (Ghi nhận `OrderCreatedEvent`).
* **Bên ngoài (Asynchronous - qua Kafka):**
    * **Inventory Module:** Bị trừ tồn kho khả dụng (`inventory_summaries.available`). Bị thêm record giữ hàng (`stock_reservations`).
    * **Notification Module:** Gửi In-app notification "Đơn hàng đang chờ thanh toán".
    * **Cassandra:** Lưu log thay đổi trạng thái thành PENDING.

#### Kịch bản 2: Khách hàng thanh toán thành công (Update Payment/Order)
* **Nội bộ (Synchronous):**
    * **UPDATE** `payment_transactions` thành SUCCESS.
    * **UPDATE** `orders` thành PAID.
    * **CREATE** `outbox_events_order` (Ghi nhận `OrderPaidEvent`).
* **Bên ngoài (Asynchronous):**
    * **Inventory Module:** Chốt trừ kho vật lý (`inventory_summaries.on_hand`). Ghi sổ cái (`inventory_ledger`).
    * **Shipment Module:** **CREATE** bản ghi `shipments` (chờ đóng gói, gọi API đối tác giao hàng).
    * **Neo4j GraphDB:** Tạo liên kết `[:BOUGHT]` giữa User và danh sách SKU để AI học hỏi.
    * **Notification Module:** Bắn Email/Push hóa đơn điện tử cho khách.

#### Kịch bản 3: Hệ thống Hủy đơn hàng (Update Order to CANCELLED)
Xảy ra khi hết hạn thanh toán, Admin tự hủy, hoặc Inventory báo lỗi hết hàng thực tế.
* **Nội bộ (Synchronous):**
    * **UPDATE** `orders` thành CANCELLED.
    * **UPDATE** hoàn trả `vouchers` (Cộng lại `used_quantity`), update `user_voucher_wallets` về COLLECTED.
    * **CREATE** `outbox_events_order` (Ghi nhận `OrderCancelledEvent`).
* **Bên ngoài (Asynchronous):**
    * **Inventory Module:** **UPDATE** `stock_reservations` (hủy giữ hàng). Hoàn trả số lượng ảo `available` để người khác có thể mua.
    * **Payment Module (Nếu đã lỡ trừ tiền):** Kích hoạt gọi API hoàn tiền (Refund) về ví Momo/Credit Card của khách. Cập nhật `payment_transactions` thành REFUNDED.
    * **Notification Module:** Gắn cờ đỏ thông báo "Đơn hàng của bạn đã bị hủy".