package io.github.nguyennhatquang.fashion.Catalog.domain.entity;

import org.springframework.data.mongodb.core.mapping.Field;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductSeo {

    @Field("meta_title")
    private String metaTitle;

    @Field("meta_description")
    private String metaDescription;
}