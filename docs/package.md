Tầng / Chức năng
Công nghệ áp dụng
Tên Dependency (Maven/Gradle)
Vai trò trong Modular Monolith + Clean Architecture
1. Cốt Lõi & Kiến Trúc
Spring Boot 3.x, Spring Modulith
spring-boot-starter-web, spring-modulith-starter-core, spring-modulith-starter-test
Đóng vai trò cấu trúc dự án. Spring Modulith là vũ khí bí mật giúp tự động kiểm tra xem các module (Order, Product) có gọi lén qua DB hay vi phạm Clean Architecture không.
2. Lưu Trữ Dữ Liệu (Polyglot)
PostgreSQL (Master/Slave), MongoDB
spring-boot-starter-data-jpa, spring-boot-starter-data-mongodb, org.postgresql:postgresql
Đặt ở tầng Infrastructure. Domain và Application (Use Case) chỉ định nghĩa Interface Repository, Data JPA/Mongo sẽ implement chúng.
3. Bộ Nhớ Đệm (Caching)
Redis
spring-boot-starter-data-redis, org.redisson:redisson (cho Distributed Lock)
Lưu cache ở tầng Infrastructure. Redisson rất quan trọng để khóa giao dịch (tránh race condition khi trừ tồn kho Flash Sale).
4. Tìm Kiếm & AI (RAG)
Elasticsearch, Spring AI, Vector DB (PGVector), LLM (Gemini / OpenAI)
spring-boot-starter-data-elasticsearch, spring-ai-core, spring-ai-pgvector-store-spring-boot-starter,
spring-ai-vertex-ai-gemini-spring-boot-starter,
spring-ai-tika-document-reader
RAG Pipeline: Dùng Spring AI để chuyển data (ví dụ: mô tả áo sơ mi) thành Vector, lưu vào PGVector. Khi user hỏi AI, truy xuất vector gần nhất và đưa cho LLM (như Gemini) tạo câu trả lời tự nhiên.
5. Bất Đồng Bộ & Sự Kiện
Spring ApplicationEvents, Apache Kafka
spring-modulith-events-core, spring-kafka
Giữa các module giao tiếp bằng Spring Events (nội bộ). Với tác vụ nặng (gửi email, đồng bộ Elasticsearch, xử lý đơn hàng hàng loạt), đẩy message ra Kafka.
6. Bảo Mật & Xác Thực
Spring Security, Keycloak (OAuth2), JWT
spring-boot-starter-oauth2-resource-server, spring-boot-starter-security
Nằm ở tầng Infrastructure / Web (Adapter). Bảo vệ các endpoint. Module User có thể gọi ra Keycloak để quản lý định danh.
7. Lưu Trữ File
SeaweedFS
software.amazon.awssdk:s3 (Khuyên dùng) hoặc spring-boot-starter-webflux (Nếu gọi REST HTTP)
Lưu hình ảnh thời trang. Interface StoragePort nằm ở Application layer, implementation bằng AWS SDK nằm ở Infrastructure layer.
9. Database Migration
Flyway hoặc Liquibase
org.flywaydb:flyway-core, org.flywaydb:flyway-database-postgresql
Bắt buộc phải có để quản lý phiên bản (version control) cho cấu trúc bảng PostgreSQL. Tự động chạy script khi khởi động Spring Boot.
Tools

Lombok ,Spring Boot DevTools ,org.mapstruct:mapstruct,com.fasterxml.jackson.core,io.vavr:vavr,org.springdoc:springdoc-openapi-starter-webmvc-ui,spring-modulith-starter-core,org.hibernate:hibernate-envers.,diffplug/spotless ,javax.persistence.AttributeConverter ,jackson-module-parameter-names
Giao tiếp hiệu năng cao
gRPC (với Spring gRPC)
org.springframework.grpc:spring-grpc-spring-boot-starter
Đóng vai trò là Input Adapter trong Clean Architecture. Thay vì chỉ có REST Controller, bạn có thêm gRPC Service để các module hoặc mobile app gọi vào.
Serialization
Protocol Buffers (proto3)
com.google.protobuf:protobuf-java
Định nghĩa cấu trúc dữ liệu chung (Data Contract) cho toàn hệ thống.