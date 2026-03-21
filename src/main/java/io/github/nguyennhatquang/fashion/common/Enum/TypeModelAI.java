package io.github.nguyennhatquang.fashion.common.Enum;

import lombok.Getter;

@Getter
public enum TypeModelAI {
    BGE_M3("bge-m3", 1024),
    NOMIC_EMBED_TEXT("nomic-embed-text", 768),
    BGE_BASE("bge-base", 768);

    private final String modelName;
    private final int dimensions;

    TypeModelAI(String modelName, int dimensions) {
        this.modelName = modelName;
        this.dimensions = dimensions;
    }

    public String getModelName() {
        return modelName;
    }

    public int getDimensions() {
        return dimensions;
    }
}
