package io.github.nguyennhatquang.fashion.common.Payload.Category;

import com.google.auto.value.AutoValue.Builder;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeleteCategoryPayload {
    String Categoryid;
}
