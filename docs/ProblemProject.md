Tên vấn đề
Vấn đề đó là gì?
Giải thích vấn đề (Bối cảnh & Thách thức)
Vấn đề cần giải quyết là gì? (Mục tiêu Kỹ thuật)
1. High-Concurrency Flash Sales
Chịu tải hệ thống và chống bán lố (Overselling) khi lượng truy cập tăng đột biến.
Trong Flash Sale, hàng ngàn requests (có thể lên tới 150k RPS) sẽ đồng loạt gọi API check kho và đặt hàng. Nếu truy vấn trực tiếp vào Database quan hệ, hệ thống sẽ sập hoặc xảy ra tình trạng "Race Condition" dẫn đến bán vượt số lượng tồn kho thực tế.
Cần thiết lập cơ chế đẩy tồn kho lên RAM/Cache (Redis). Xử lý logic khóa (Distributed Lock) hoặc Atomic operations (LUA script) để trừ kho nhanh chóng. Xây dựng hàng đợi (Message Queue) để xử lý đơn hàng bất đồng bộ.
2. Complex SKU Inventory & Multi-Warehouse
Quản lý ma trận sản phẩm đa biến thể và đồng bộ tồn kho đa kho.
Quần áo có ma trận thuộc tính phức tạp (Màu x Size x Chất liệu = hàng chục SKU cho 1 sản phẩm). Cần linh hoạt khi query danh mục, lọc thuộc tính động. Đồng thời, phải track chính xác số lượng tồn của từng mã SKU nằm ở các kho (warehouse) khác nhau trên thời gian thực (Real-time).
Xây dựng schema Database linh hoạt cho Dynamic Attributes (có thể cân nhắc NoSQL hoặc JSONB trong PostgreSQL). Viết logic tổng hợp tồn kho từ nhiều kho để hiển thị trên PDP và thiết kế luồng khóa/giữ hàng (Reserve Stock) chính xác.
3. ACID-Compliant Distributed Transactions
Đảm bảo tính toàn vẹn dữ liệu trong luồng thanh toán chéo nhiều dịch vụ.
Luồng Checkout gọi nhiều module độc lập: Cập nhật Giỏ hàng -> Áp Voucher -> Giữ hàng (Inventory) -> Gạch nợ/Thanh toán (Payment Gateway). Nếu cổng thanh toán lỗi hoặc người dùng hủy, hệ thống phải trả lại tồn kho và voucher chính xác, không được sai lệch dữ liệu.
Triển khai Saga Pattern (Choreography hoặc Orchestration) để quản lý Distributed Transaction. Viết các cơ chế Compensating Action (hành động bù trừ/rollback) để đảm bảo tính ACID khi có 1 node trong chuỗi bị lỗi.
4. Modular API Architecture
Cô lập các domain logic để hệ thống dễ bảo trì và mở rộng.
Nếu gộp tất cả logic (Order, Product, User, RAG) vào một cục (Spaghetti code), khi một module sập (ví dụ RAG quá tải) sẽ kéo theo luồng thanh toán sập. Việc test và nâng cấp từng tính năng cũng sẽ rất rủi ro.
Triển khai Modular Monolithic (hoặc Microservices). Chia tách ranh giới rõ ràng cho Auth, Catalog, Inventory, Order, AI. Đảm bảo luồng xác thực (JWT/OAuth) giao tiếp xuyên suốt và an toàn giữa các module này.
5. Multimodal AI RAG Integration
Xử lý đa phương thức (Ảnh, Text, Voice) để tìm kiếm và tư vấn.
Truy vấn bằng ảnh upload (Visual Search) hoặc mô tả phức tạp bằng giọng nói/text đòi hỏi hệ thống phải hiểu ngữ cảnh (Context) thay vì chỉ match từ khóa truyền thống. Dữ liệu sản phẩm và bài viết phải được số hóa để AI hiểu.
Bóc tách đặc điểm ảnh và text, chuyển đổi thành Vector Embeddings. Đồng bộ dữ liệu này vào Vector Database. Xây dựng luồng RAG kết hợp LLM để tìm sản phẩm tương tự hoặc mix-match outfit chuẩn xác từ kho hàng đang có sẵn.
6. AI Persona & Guardrails Control
Kiểm soát tính cách và luồng trả lời của Trợ lý AI.
Trợ lý AI có thể bị người dùng điều hướng trả lời các vấn đề ngoài lề (chính trị, tôn giáo, code) hoặc đưa ra những phong cách tư vấn không phù hợp với định vị của thương hiệu. AI cũng có thể ảo giác (Hallucination) khuyên khách mua hàng không có trong kho.
Thiết lập Prompt Tuning và Guardrails ở trang Admin. Ép AI chỉ được nội suy từ Vector Knowledge Base của cửa hàng. Phân loại và chặn (Filter) ngay lập tức các intent nằm ngoài ngữ cảnh Fashion E-commerce.
7. Real-time Monitoring & Dashboarding
Theo dõi trạng thái hệ thống và luồng tiền/hàng theo thời gian thực.
Trang Giám sát Flash Sale cần số liệu "đang sống". Admin không thể f5 liên tục để xem số lượng hàng vơi đi hoặc xem server có đang bị nghẽn hay không. Khách hàng ở Order History cũng cần biết trạng thái đơn (đang giao, hủy) ngay tức khắc.
Sử dụng WebSocket hoặc Server-Sent Events (SSE) để push dữ liệu từ server xuống client (Admin Dashboard & User History) theo thời gian thực mà không làm tăng tải query vào Database chính.
Tên vấn đề
Vấn đề đó là gì?
Giải thích vấn đề (Bối cảnh & Thách thức)
Vấn đề cần giải quyết là gì? (Mục tiêu Kỹ thuật)
8. Payment Idempotency & Webhook Handling
Đảm bảo tính "lũy đẳng" và nhận kết quả chính xác từ Cổng thanh toán bên thứ 3.
Ở luồng Thanh toán (Checkout - Phần B), bạn gọi cổng bên thứ 3 (như VNPay, Momo, Stripe). Nếu rớt mạng, khách hàng có thể bấm nút thanh toán nhiều lần. Ngoài ra, webhook từ đối tác trả kết quả về có thể bị delay hoặc gọi trùng lặp (duplicate calls), dẫn đến rủi ro gạch nợ sai hoặc cập nhật sai trạng thái đơn hàng.
Thiết kế API có tính Idempotency (Lũy đẳng) bằng cách gắn Idempotency-Key cho mỗi giao dịch. Dù user/hệ thống gọi API bao nhiêu lần, hệ thống chỉ xử lý trừ tiền 1 lần duy nhất. Xây dựng endpoint nhận Webhook bảo mật, có xác thực chữ ký (Signature verification).
9. Stateless Authentication & Authorization
Quản lý phiên đăng nhập và phân quyền xuyên suốt kiến trúc Modular.
Ở phần (C) Tài khoản, bạn có JWT/OAuth. Trong hệ thống Modular (hoặc Microservices), khi user gọi API từ Storefront sang Order, rồi RAG, làm sao để xác thực user liên tục mà không bắt Database phải check token liên tục? Hơn nữa, Admin (Phần D) cần quyền riêng (Role-Based Access Control) hoàn toàn tách biệt với Khách hàng.
Triển khai Stateless Authentication với JWT. Cấu hình một lớp API Gateway hoặc Middleware chung để decode và verify JWT Token trước khi đẩy request xuống các module bên dưới. Thiết kế RBAC (Role-Based Access Control) chuẩn xác để chặn user thường truy cập vào API của Admin.