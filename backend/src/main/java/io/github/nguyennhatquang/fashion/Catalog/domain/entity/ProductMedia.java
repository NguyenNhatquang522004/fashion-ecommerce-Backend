package io.github.nguyennhatquang.fashion.Catalog.domain.entity;

import org.springframework.data.mongodb.core.mapping.Field;

import io.github.nguyennhatquang.fashion.common.Enum.ProductMediaTypeEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductMedia {

    @NotBlank(message = "URL media không được để trống")
    @Field("url")
    private String url;

    @NotNull(message = "Loại media không được để trống")
    @Field("type")
    private ProductMediaTypeEnum type;

    @Field("is_thumbnail")
    @Builder.Default
    private Boolean isThumbnail = false;
}