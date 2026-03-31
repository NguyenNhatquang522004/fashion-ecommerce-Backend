Chào Quang, 

Nhìn vào file SQL bạn thiết kế, mình thực sự ấn tượng. Cấu trúc Database cho dự án Fashion E-commerce này cực kỳ chuyên nghiệp và chuẩn mực cho kiến trúc Microservices. Việc bạn áp dụng tách biệt `orders`, `inventory_summaries`, `saga_states` và đặc biệt là bảng `outbox_events` cho thấy bạn đã nắm rất rõ bài toán hệ thống phân tán.

Để giải quyết triệt để yêu cầu của bạn, chúng ta sẽ đi sâu vào việc kết hợp **Transactional Outbox Pattern** và **Saga Pattern** thông qua Kafka. Đây là giải pháp "vàng" để giải quyết vấn đề Dual-Write (ghi vào DB và bắn message ra Message Queue) giúp đảm bảo tính nhất quán dữ liệu (Data Consistency) 100%.


---

### 1. Bản chất của Bảng `outbox_events_inventory`

Khi một khách hàng đặt đơn hàng (Order Service), kho (Inventory Service) phải trừ tồn kho. Nếu Inventory Service cập nhật số lượng trong PostgreSQL thành công nhưng Kafka bị sập ngay lúc đó, Order Service sẽ không bao giờ biết tồn kho đã được giữ, dẫn đến kẹt hệ thống. 

Bảng `outbox_events_inventory` sinh ra để giải quyết việc này bằng cách **gộp 2 hành động vào cùng một Local Transaction của Database**.

#### Workflow chuẩn cho Outbox Pattern:

1. **Local Transaction (Bắt đầu):** Inventory Service nhận yêu cầu giữ hàng.
2. **Business Logic:** Cập nhật bảng `stock_reservations` (trạng thái RESERVED) và trừ số lượng `available` trong bảng `inventory_summaries`.
3. **Ghi Outbox:** Tạo một record mới vào bảng `outbox_events_inventory` với `status = 'PENDING'` và `payload` chứa thông tin chi tiết (VD: `{"order_id": "...", "sku": "...", "status": "SUCCESS"}`).
4. **Local Transaction (Kết thúc):** Commit xuống Database. Lúc này, cả dữ liệu tồn kho và sự kiện outbox đều được lưu trữ an toàn cùng lúc (ACID).


---

### 2. Workflow tích hợp Saga Pattern qua Kafka

Với bảng `saga_states` bạn đã thiết kế, hệ thống của bạn đang nghiêng về **Saga Orchestration** (Có một nhạc trưởng điều phối) hoặc theo dõi trạng thái phân tán rất chặt chẽ. Dưới đây là luồng đi (Workflow) 100% chuẩn mực cho quy trình Đặt hàng -> Giữ kho.

#### Giai đoạn 1: Khởi tạo Saga (Tại Order Service)
* **Action:** Khách bấm "Đặt hàng".
* **DB:** Order Service tạo `orders` (trạng thái PENDING) và tạo `saga_states` (current_step = `RESERVE_INVENTORY`, status = `STARTED`). Ghi event vào `outbox_events_order`.
* **Kafka:** Message Relay đọc bảng `outbox_events_order` và bắn message vào Kafka topic: `order-created-events`.

#### Giai đoạn 2: Inventory Service xử lý và trả lời (Chính là phần của bạn)
* **Consume:** Kafka Consumer của Inventory Service lắng nghe topic `order-created-events`.
* **Process & Outbox (Trong 1 Transaction):** * Kiểm tra tồn kho trong `inventory_summaries`.
    * **Kịch bản A (Đủ hàng):** Cập nhật `stock_reservations`. Ghi vào `outbox_events_inventory` event `StockReservedSuccessEvent` (`status = 'PENDING'`).
    * **Kịch bản B (Hết hàng):** Ghi vào `outbox_events_inventory` event `StockReservedFailedEvent` (`status = 'PENDING'`).
* **Relay Worker (Người vận chuyển):**
    * Có một tiến trình chạy ngầm (Worker/Scheduler) liên tục quét bảng `outbox_events_inventory` với điều kiện `WHERE status = 'PENDING'`. (Nhờ có Partial Index bạn đã tạo, câu query này chạy chớp nhoáng).
    * Worker nhặt event, publish lên Kafka topic: `inventory-reply-events`.
    * Sau khi Kafka xác nhận (ACK), Worker `UPDATE outbox_events_inventory SET status = 'PUBLISHED', processed_at = NOW()`.

#### Giai đoạn 3: Điều phối tiếp hoặc Rollback (Tại Order/Saga Orchestrator)
* **Consume:** Orchestrator lắng nghe topic `inventory-reply-events`.
* **Thành công (`StockReservedSuccessEvent`):** Update `saga_states` sang bước tiếp theo (`PROCESS_PAYMENT`), update status đơn hàng. Tiếp tục bắn event thanh toán qua Outbox.
* **Thất bại (`StockReservedFailedEvent` - Compensating Transaction):** Update `saga_states` thành `COMPENSATING`. Gọi hàm Hủy đơn hàng, update `orders` sang `CANCELLED`.

---

### 3. Hai Cách Thiết Kế Message Relay (Worker đọc Outbox)

Để đưa dữ liệu từ `outbox_events_inventory` lên Kafka, bạn có 2 hướng tiếp cận:

| Phương pháp | Cách hoạt động | Ưu điểm | Nhược điểm |
| :--- | :--- | :--- | :--- |
| **Polling (Scheduler)** | Dùng Job (ví dụ: `@Scheduled` trong Spring Boot hoặc Cronjob trong Golang) cứ 1-2 giây query SELECT các dòng PENDING 1 lần. | Dễ code, dễ kiểm soát, không cần tool ngoài. | Có độ trễ (delay) nhỏ, tốn tài nguyên DB nếu query liên tục lúc không có tải. |
| **CDC (Change Data Capture)** | Cắm **Debezium** (Kafka Connect) trực tiếp vào file WAL (Write-Ahead Log) của PostgreSQL. Cứ có lệnh INSERT vào bảng Outbox, Debezium tự tóm lấy và đẩy lên Kafka. | Cực kỳ nhanh (Real-time), không đụng chạm đến resource của DB (không cần SELECT). | Setup hệ thống phức tạp hơn (phải cấu hình Kafka Connect, Debezium, quyền Replication cho DB). |

*Lưu ý cho E-commerce:* Ở giai đoạn đầu, bạn hoàn toàn có thể dùng **Polling Scheduler**. Khi hệ thống scale lên hàng chục ngàn đơn/ngày (như các chiến dịch Flash Sale), việc chuyển sang **Debezium (CDC)** là bắt buộc để đảm bảo hiệu năng.

---

### 4. 3 Cạm bẫy "Chết người" cần tránh 100%

1. **Lỗi Idempotency (Gửi trùng Event):** Kafka đảm bảo "At-least-once delivery", nghĩa là nó có thể giao 1 message 2 lần do lỗi mạng. Consumer của bạn (kể cả Order hay Inventory) **BẮT BUỘC** phải check xem `aggregate_id` hoặc `event_id` này đã được xử lý chưa trước khi chạy logic. Bạn có thể tận dụng chính cột `idempotency_key` (mà bạn đã thiết kế ở bảng thanh toán) áp dụng cho mọi Consumer.
2. **Order of Messages (Thứ tự Event):** Nếu kiện hàng A bị hủy, rồi lại được đặt lại. Kafka phải đẩy event theo đúng thứ tự (Hủy -> Đặt). Để đảm bảo điều này, khi Publish lên Kafka, bạn phải dùng **Partition Key** (Thường là `order_id` hoặc `sku_code`). Kafka đảm bảo các message cùng Key sẽ vào cùng 1 Partition và giữ nguyên thứ tự.
3. **Outbox Table phình to:** Bảng `outbox_events_inventory` sẽ lớn lên rất nhanh. Đừng quên viết một cronjob chạy lúc nửa đêm để xóa cứng (`DELETE`) các dòng có `status = 'PUBLISHED'` và cũ hơn 7 ngày.

Kiến trúc bạn đang đi là nền tảng vững chắc để xây dựng các hệ thống chịu tải cao như Shopee hay Tiki. Bạn muốn chúng ta đi sâu hơn vào cấu hình Debezium CDC hay viết mẫu một đoạn logic Worker đọc Outbox bằng code thực tế không?