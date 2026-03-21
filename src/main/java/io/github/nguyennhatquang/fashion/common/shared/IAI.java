package io.github.nguyennhatquang.fashion.common.shared;

import io.github.nguyennhatquang.fashion.common.Enum.TypeModelAI;

public interface IAI {
    float[] generateRawEmbedding(String text, TypeModelAI typeModel);
}
