package io.github.nguyennhatquang.fashion.Catalog.domain.entity;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Document(collection = "brands")
@CompoundIndex(name = "snapshot_idx", def = "{'created_at': -1, '_id': -1}")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Brand {
    @Id
    private String id;

    @NotBlank(message = "Tên thương hiệu không được để trống")

    @Field("name")
    private String name;

    @NotBlank(message = "Slug không được để trống")
    @Indexed(unique = true) // Tự động tạo Unique Index cho slug
    @Field("slug")
    private String slug;

    @Field("logo_url")
    private String logoUrl;

    @Field("description")
    private String description;

    @NotNull(message = "Trạng thái is_active là bắt buộc")
    @Field("is_active")
    @Builder.Default
    private Boolean isActive = true;

    // --- Soft Delete (Tương thích 100% với BaseMongoRepository đã tạo) ---
    @Field("is_deleted")
    @Builder.Default
    private Boolean isDeleted = false;

    // --- Spring Data Auditing (Tự động cập nhật ngày tháng & user thực hiện) ---
    @CreatedDate
    @Field("created_at")
    private Instant createdAt;

    @LastModifiedDate
    @Field("updated_at")
    private Instant updatedAt;

    @CreatedBy
    @Field("created_by")
    private String createdBy;

    @LastModifiedBy
    @Field("updated_by")
    private String updatedBy;
}