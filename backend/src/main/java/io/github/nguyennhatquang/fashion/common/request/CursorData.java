package io.github.nguyennhatquang.fashion.common.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CursorData {
    private Object sortValue;
    private String id;
}
