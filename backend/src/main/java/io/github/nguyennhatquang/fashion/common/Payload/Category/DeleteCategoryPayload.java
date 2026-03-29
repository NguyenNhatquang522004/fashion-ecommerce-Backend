package io.github.nguyennhatquang.fashion.common.Payload.Category;



import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeleteCategoryPayload {
    String Categoryid;
}
