package io.github.nguyennhatquang.fashion.common.Payload.Category;



import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeleteBrandPayload {
    String BrandID;
}
