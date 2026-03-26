package io.github.nguyennhatquang.fashion.Catalog.domain.entity;

import java.time.Instant;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Document(collection = "categories")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@CompoundIndex(name = "snapshot_idx", def = "{'created_at': -1, '_id': -1}")
public class Category {
    @Id
    private String id;

    @NotBlank(message = "Tên danh mục không được để trống")
    @Field("name")
    private String name;

    @NotBlank(message = "Slug không được để trống")
    @Indexed(unique = true)
    @Field("slug")
    private String slug;

    // Lưu dưới db là ObjectId, nhưng trong Java dùng String để dễ xử lý API
    @Field(targetType = FieldType.OBJECT_ID, value = "parent_id")
    private String parentId;

    @NotBlank(message = "Path không được để trống")
    @Indexed
    @Field("path")
    private String path;

    @Field("image_url")
    private String imageUrl;

    @NotNull(message = "Trạng thái is_active là bắt buộc")
    @Field("is_active")
    @Builder.Default
    private Boolean isActive = true;

    @Field("sort_order")
    @Builder.Default
    private Integer sortOrder = 0;

    @CreatedBy
    @Field("created_by")
    private String createdBy;

    @LastModifiedBy
    @Field("updated_by")
    private String updatedBy;

    // Sử dụng Instant để chuẩn hóa UTC timezone thay vì Date hay LocalDateTime
    @CreatedDate
    @Field("created_at")
    private Instant createdAt;

    @LastModifiedDate
    @Field("updated_at")
    private Instant updatedAt;

    @Field("is_deleted")
    @Builder.Default
    private Boolean isDeleted = false;
}
