package io.github.nguyennhatquang.fashion.Catalog.domain.entity;

import org.springframework.data.mongodb.core.mapping.Field;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductAttribute {

    @NotBlank(message = "Tên thuộc tính không được để trống")
    @Field("name")
    private String name; // vd: "Color"

    @NotEmpty(message = "Phải có ít nhất 1 tùy chọn cho thuộc tính")
    @Field("options")
    private List<String> options; // vd: ["Red", "Blue"]
}