

### 1. Giỏ hàng: `ShoppingCart` & `CartItem` (Redis)
Đây là điểm chạm đầu tiên, hoạt động độc lập với RDBMS cho đến khi người dùng quyết định thanh toán.

* **Create/Update (Thêm/Sửa giỏ hàng):** * **Read:** Truy vấn nhẹ nhàng xuống MongoDB (`products`) để lấy thông tin cơ bản và PostgreSQL (`inventory_summaries`) để đảm bảo sản phẩm còn hàng (`available > 0`) trước khi cho phép thêm vào giỏ.
    * **Không ảnh hưởng:** Tồn kho thực tế (PostgreSQL) CHƯA bị trừ.
* **Delete (Xóa giỏ hàng):**
    * **Kích hoạt:** Xảy ra khi người dùng tự xóa, Redis hết hạn (TTL), hoặc khi người dùng **nhấn nút "Đặt hàng" (Checkout)**.
    * **Ảnh hưởng chéo:** Thao tác Checkout sẽ chuyển đổi dữ liệu từ `CartItem` sang việc tạo mới `Order` và `OrderItem` dưới PostgreSQL.

### 2. Khuyến mãi: `Voucher`, `PromotionRule` & `UserVoucherWallet`
Các entity này bị khóa chặt bởi nghiệp vụ kiểm soát số lượng (Concurrency Control).

* **Create (Lưu mã giảm giá):**
    * Tạo mới 1 record trong `UserVoucherWallet` (Trạng thái: `COLLECTED`). 
    * Kiểm tra logic: Đảm bảo User chưa lưu mã này trước đó.
* **Update (Áp dụng Voucher lúc Checkout):**
    * **Read `Voucher`:** Kiểm tra điều kiện (`min_order_value`), ngày hết hạn (`start_time`, `end_time`).
    * **Update `Voucher`:** Tăng `used_quantity = used_quantity + 1`. Áp dụng **Optimistic Locking** (`version`) để chặn lỗi 2 người cùng xài mã cuối cùng.
    * **Update `UserVoucherWallet`:** Chuyển trạng thái từ `COLLECTED` sang `USED`.
* **Rollback/Update (Hủy đơn hàng):**
    * Nếu `Order` bị hủy, hệ thống phải hoàn trả lại Voucher.
    * **Update:** Giảm `Voucher.used_quantity` và cập nhật `UserVoucherWallet` về lại `COLLECTED`.

### 3. Đơn hàng gốc: `Order` & `OrderItem`
Đây là trung tâm của mọi luồng giao dịch phân tán (Distributed Transaction) .

* **Create (Tạo đơn hàng mới):** * **Read `Product` (MongoDB):** Chụp lại Snapshot tên sản phẩm, giá cả ngay thời điểm đó để lưu cứng vào `OrderItem` (đảm bảo sau này giá thay đổi thì đơn cũ không bị lệch tiền).
    * **Cùng một Local Transaction (Giao dịch cục bộ), hệ thống BẮT BUỘC phải tạo đồng thời:**
        1.  Tạo `Order` (Status: `PENDING`).
        2.  Tạo các `OrderItem`.
        3.  Update `UserVoucherWallet` (thành `USED`).
        4.  Tạo `OutboxEvent` (Loại event: `OrderCreatedEvent` chứa danh sách SKU).
        5.  Tạo `SagaState` (Status: `STARTED`, Step: `RESERVE_INVENTORY`).
* **Update (Thay đổi trạng thái Đơn hàng):**
    * Trạng thái `Order` hiếm khi được user tự CRUD, mà được "đẩy" bởi các Entity khác.
    * `PENDING` -> `RESERVED`: Sau khi Kafka gửi `OrderCreatedEvent`, Module Inventory đọc được và giữ kho thành công, nó bắn lại event, Update Order sang `RESERVED`.
    * `RESERVED` -> `PAID`: Được kích hoạt khi `PaymentTransaction` cập nhật thành công.
    * `PAID` -> `PROCESSING`: Kích hoạt việc **Create `Shipment`** (Tạo vận đơn).
* **Delete (Không bao giờ Hard Delete):**
    * Thay vào đó là **Update** status thành `CANCELLED`.
    * Kích hoạt dây chuyền: Tạo `OutboxEvent` -> Module Inventory nhận event để nhả tồn kho (`available = available + quantity`) -> Hoàn Voucher.

### 4. Giao dịch tài chính: `PaymentTransaction`
Đóng vai trò chốt chặn cuối cùng cho dòng tiền.

* **Create (Khởi tạo thanh toán):**
    * Tạo khi user chọn phương thức thanh toán (VNPAY/Momo). Status là `PENDING`. Lưu `order_id` và sinh ra `idempotency_key`.
* **Update (Nhận Webhook từ cổng thanh toán):**
    * **Read:** Check `idempotency_key` và `provider_transaction_id`. Nếu đã xử lý rồi -> Bỏ qua (Tránh cộng/trừ tiền 2 lần).
    * **Update:** Chuyển status thành `SUCCESS` hoặc `FAILED`.
    * **Ảnh hưởng chéo:** Cùng 1 Transaction Database, nếu `SUCCESS`, lập tức **Update `Order`** sang `PAID` và **Create `OutboxEvent`** (`OrderPaidEvent`) để nhả hóa đơn và kích hoạt đóng gói.

### 5. Giao hàng: `Shipment`
Thực thể này tách biệt để hệ thống vận hành trơn tru ngay cả khi đối tác giao hàng lag.

* **Create (Đẩy đơn qua đối tác):**
    * Chỉ được tạo khi `Order` đang ở trạng thái `PAID` hoặc `COD_CONFIRMED`.
    * Lưu `tracking_code` từ API của GHN/GHTK.
* **Update (Hành trình đơn hàng):**
    * Webhook từ đối tác bắn về liên tục để cập nhật Status (`PICKED_UP`, `IN_TRANSIT`, `DELIVERED`).
    * **Ảnh hưởng chéo:** Khi `Shipment` = `DELIVERED`, nó sẽ cập nhật `Order` thành `COMPLETED`. Quá trình này có thể kích hoạt tiếp `OutboxEvent` để cộng điểm Loyalty cho khách.

### 6. Điều phối hệ thống: `SagaState` & `OutboxEvent`
Đây là Infrastructure Entities, ẩn dưới nghiệp vụ kinh doanh nhưng quyết định sự sống còn của dữ liệu.

* **Create:** * Luôn luôn được **Create chung** trong cùng một câu lệnh `COMMIT` của Database với các Entity chính (`Order`, `Payment`). Điều này đảm bảo: Nếu `Order` lưu thành công, chắc chắn có `OutboxEvent` để bắn đi. Nếu lưu `Order` lỗi, event cũng không được tạo.
* **Update/Delete:**
    * **`OutboxEvent`:** Một Cronjob/Worker sẽ liên tục Read các dòng `PENDING`, gửi payload lên Kafka, sau đó **Update** thành `PUBLISHED` (hoặc Delete để dọn dẹp bảng).
    * **`SagaState`:** Lắng nghe Kafka để **Update** tiến trình (Vd: từ `RESERVE_INVENTORY` sang `PROCESS_PAYMENT`). Nếu có 1 bước lỗi (Vd: Thanh toán hỏng), nó Update thành `COMPENSATING` và bắn event lệnh Rollback toàn bộ (Nhả kho, Hủy đơn).

