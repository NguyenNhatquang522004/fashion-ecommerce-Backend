Chào Quang, với hệ thống kiến trúc Microservices hướng sự kiện (Event-Driven) mà bạn đang xây dựng cho dự án Fashion E-commerce, việc định nghĩa rõ ràng chức năng trên trang Admin (Back-office) và luồng dữ liệu (Workflow) là cực kỳ quan trọng. Nó đảm bảo tính toàn vẹn dữ liệu giữa PostgreSQL, Redis và Kafka.

Dưới đây là thiết kế chi tiết 100% cho từng bảng, các chức năng Admin tương ứng và luồng CRUD tác động chéo.

---

### 1. Bảng `Warehouse` & `StockLocation` (Quản lý Kho bãi vật lý)

**Chức năng trên trang Admin:**
* **Quản lý danh sách kho:** Thêm mới kho bãi, cập nhật địa chỉ, vô hiệu hóa (Deactivate) kho không còn hoạt động.
* **Sơ đồ kho (Topology):** Mapping vật lý các Zone (Khu), Aisle (Lối đi), Rack (Kệ), Shelf (Tầng) để nhân viên lấy hàng nhanh gọn.

**Tác động cơ sở dữ liệu (Impact):**
* **Create/Read/Update:** Chỉ ghi nhận/sửa đổi trên 2 bảng này.
* **Delete (Vô hiệu hóa):** Không bao giờ xóa cứng (Hard Delete). Khi Admin nhấn "Deactivate" một kho, hệ thống phải kiểm tra bảng `inventory_summaries` (hoặc view tồn kho). Nếu tổng tồn kho (`on_hand`) của kho đó > 0, **chặn không cho vô hiệu hóa** và báo lỗi.

**Workflow (Tạo kho mới):**
1. Admin điền thông tin kho bãi và sơ đồ kệ.
2. Backend validate tính hợp lệ.
3. Insert dữ liệu vào `warehouses` và hàng loạt bản ghi vào `stock_locations`.

---

### 2. Bảng `InventoryLedger` (Sổ cái kiểm toán & Nhập xuất tồn)

**Chức năng trên trang Admin:**
* **Nhập kho (Stock-In) / Xuất kho (Stock-Out) thủ công:** Admin hoặc Thủ kho upload file Excel hoặc quét mã vạch để nhập số lượng hàng mới về.
* **Kiểm kê kho (Stock Take):** Điều chỉnh sai lệch giữa phần mềm và thực tế (do mất cắp, hư hỏng).
* **Xem lịch sử (Audit):** Màn hình chỉ đọc (Read-only) hiển thị toàn bộ log biến động của một SKU. **Tuyệt đối không có nút Edit hay Delete cho dòng lịch sử.**

**Tác động cơ sở dữ liệu (Impact):**
* Cập nhật chéo với bảng `inventory_summaries` (bảng lưu tổng số lượng hiện tại, nếu bạn có thiết kế bảng này).

**Workflow (Nhập hàng mới vào kho - Cực kỳ quan trọng):**
1. Admin tạo "Phiếu nhập kho" (Purchase Order).
2. Backend mở **Database Transaction**.
3. Insert vào bảng `inventory_ledger` với `transaction_type = 'STOCK_IN'`, `quantity_change = +100`.
4. Update bảng `inventory_summaries`: `SET on_hand = on_hand + 100, available = available + 100 WHERE sku_code = XYZ`.
5. Insert vào bảng `OutboxEvent` (VD: `InventoryUpdatedEvent`) để báo cho các service khác (như Search Engine cập nhật lại trạng thái "Còn hàng").
6. **Commit Transaction.**

---

### 3. Bảng `FlashSaleCampaign` (Quản lý Chiến dịch Khuyến mãi)

**Chức năng trên trang Admin:**
* **Tạo/Sửa Campaign:** Đặt tên, thời gian bắt đầu/kết thúc.
* **Quản lý SKU:** Chọn sản phẩm, set giá Flash Sale (phải nhỏ hơn giá gốc), cấu hình tổng số lượng bán ra (Quota), giới hạn mua mỗi user.
* **Nút "Publish" (Đồng bộ lên Redis):** Kích hoạt đẩy dữ liệu từ Database lên RAM.

**Tác động cơ sở dữ liệu (Impact):**
* Ghi vào PostgreSQL (`flash_sale_campaigns`, `flash_sale_items`).
* Ghi đè lên cấu trúc dữ liệu của **Redis** khi được Publish.

**Workflow (Từ lúc tạo đến lúc chạy):**
1. **Draft:** Admin thêm campaign và SKU. Dữ liệu chỉ nằm ở PostgreSQL.
2. **Publish (Khởi động chiến dịch):** Admin nhấn nút Publish trước giờ G.
3. Backend đọc danh sách SKU từ PostgreSQL.
4. Đẩy lên Redis tạo `InventoryCache` và `FlashSaleQuota` (VD: key `flashsale:campaign_123:sku_ABC`, value: `1000`).
5. Update trạng thái campaign trong PostgreSQL thành `PUBLISHED`.

---

### 4. `InventoryCache` & `FlashSaleQuota` (Redis - Màn hình Monitor)

**Chức năng trên trang Admin:**
* **Real-time Dashboard:** Màn hình giám sát trực tiếp số lượng Request Per Second (RPS), lượng tồn kho đang bị trừ theo thời gian thực.
* **Kill-Switch (Dừng khẩn cấp):** Nút đỏ để Admin đóng băng chiến dịch ngay lập tức nếu phát hiện gian lận hoặc lỗi hệ thống.

**Tác động cơ sở dữ liệu (Impact):**
* Khi nhấn Kill-Switch, Backend xóa key trên Redis hoặc đổi flag `is_active = false` trên Redis để chặn toàn bộ request mua hàng từ Frontend.

**Workflow (Khách hàng giành giật Flash Sale):**
1. Hàng ngàn request đổ về. Backend dùng **Lua Script** kết hợp Redisson trên Redis.
2. Lua Script kiểm tra: Nếu Quota > 0 -> Trừ Quota đi 1 -> Trả về `SUCCESS`. Nếu = 0 -> Trả về `SOLD_OUT`. (Tất cả chạy nguyên tử - Atomic trên RAM).
3. Sau khi trừ Redis thành công, hệ thống đẩy message trực tiếp vào Kafka topic `flashsale-orders`. Một worker chạy ngầm sẽ hứng message này để insert từ từ vào bảng `orders` và `InventoryLedger` ở PostgreSQL để tránh sập DB.
f
---

### 5. Bảng `StockReservation` (Quản lý Giữ hàng tạm thời)

**Chức năng trên trang Admin:**
* **Giám sát giữ kho:** Xem có bao nhiêu đơn hàng đang trong trạng thái "Chờ thanh toán" và giữ bao nhiêu hàng.
* **Xả kho ép buộc (Force Release):** Tính năng cho phép Admin giải phóng tồn kho của một đơn hàng bị kẹt do lỗi hệ thống (hiếm khi dùng, thường để tự động).

**Tác động cơ sở dữ liệu (Impact):**
* Tác động trực tiếp lên cột `available` của bảng `inventory_summaries` và ghi nhận `inventory_ledger` (type: `RESERVE` hoặc `RELEASE`).

**Workflow (Giữ và Nhả kho tự động qua Kafka):**
1. **Giữ kho:** Có order mới -> Backend mở Transaction -> Insert `StockReservation` (expires_at = NOW + 15 phút) -> Trừ cột `available` -> Ghi `OutboxEvent` (StockReserved) -> Commit.
2. **Thanh toán thành công:** Lắng nghe Kafka `PaymentSuccessEvent` -> Đổi status Reservation thành `CONFIRMED` -> Trừ thẳng cột `on_hand`.
3. **Quá hạn (Hủy đơn):** Một cronjob (Spring Scheduler/Go Worker) hoặc Kafka Delayed Queue quét bảng `StockReservation` tìm dòng `expires_at < NOW()` và `status = RESERVED`. Tiến hành cộng lại cột `available`, update status thành `EXPIRED` và bắn event hủy đơn hàng.

---

### 6. Bảng `OutboxEvent` (Trái tim của Saga Pattern)

**Chức năng trên trang Admin (Dành cho DevOps/Admin Tech):**
* **System Health Dashboard:** Hiển thị danh sách các event bị lỗi hoặc đang kẹt (`status = PENDING` hoặc `FAILED`).
* **Manual Retry (Bắn lại sự kiện):** Nút bấm ép worker đẩy lại một message cụ thể lên Kafka nếu trước đó mạng bị lỗi.

**Tác động cơ sở dữ liệu (Impact):**
* Chỉ cập nhật trạng thái (`status`, `retry_count`, `error_message`) trong chính bảng Outbox.

**Workflow (Spring Modulith hoặc Debezium đọc Outbox):**
1. Các nghiệp vụ kinh doanh (Order, Inventory) khi làm việc xong đều insert 1 dòng vào `OutboxEvent` trong cùng một Database Transaction.
2. **Worker chạy ngầm:** Một tiến trình liên tục SELECT các dòng `WHERE status = 'PENDING'`.
3. Worker bốc data đẩy vào Kafka Topic tương ứng.
4. Nhận được phản hồi ACK từ Kafka, Worker lập tức `UPDATE OutboxEvent SET status = 'PUBLISHED', processed_at = NOW()`.
5. Xóa rác: Một job chạy lúc 2h sáng mỗi ngày `DELETE` các dòng đã PUBLISHED và cũ hơn 7 ngày để giải phóng dung lượng Database.

Bạn có muốn mình đi sâu vào việc viết đoạn mã **Lua Script cho Redis** để xử lý bài toán trừ Quota không bị âm (over-selling) ở mục số 4, hoặc cách cấu hình **Debezium** để đọc bảng Outbox này không?