package io.github.nguyennhatquang.fashion.Catalog.domain.entity;

import org.springframework.data.mongodb.core.mapping.Field;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SkuAttribute {

    @NotBlank(message = "Khóa thuộc tính không được để trống (vd: Color)")
    @Field("key")
    private String key;

    @NotBlank(message = "Giá trị thuộc tính không được để trống (vd: Red)")
    @Field("value")
    private String value;
}