package io.github.nguyennhatquang.fashion.common.Payload.Category;

import com.google.auto.value.AutoValue.Builder;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeleteBrandPayload {
    String BrandID;
}
