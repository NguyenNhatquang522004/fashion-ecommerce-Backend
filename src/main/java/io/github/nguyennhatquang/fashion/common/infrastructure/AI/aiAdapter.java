package io.github.nguyennhatquang.fashion.common.infrastructure.AI;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingRequest;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import io.github.nguyennhatquang.fashion.common.Enum.TypeModelAI;
import io.github.nguyennhatquang.fashion.common.shared.IAI;

import java.util.List;

import org.springframework.ai.ollama.api.OllamaEmbeddingOptions;

@Service
public class AiAdapter implements IAI {
    private final EmbeddingModel embeddingModel;
    private final VectorStore vectorStore;

    public AiAdapter(EmbeddingModel embeddingModel, VectorStore vectorStore) {
        this.embeddingModel = embeddingModel;
        this.vectorStore = vectorStore;
    }

    @Override
    public float[] generateRawEmbedding(String text, TypeModelAI typeModel) {
        // Trong Spring AI 2.x, bắt buộc dùng Builder pattern
        var options = OllamaEmbeddingOptions.builder()
                .model(typeModel.getModelName()) // Chỉ định model bge-m3
                .build();

        // Tạo request chứa text cần nhúng và options (chỉ định model bge-m3)
        EmbeddingRequest request = new EmbeddingRequest(List.of(text), options);

        // Gọi model để xử lý
        EmbeddingResponse response = embeddingModel.call(request);

        // Trả về mảng float[] (ví dụ: 1024 chiều cho bge-m3)
        return response.getResult().getOutput();
    }

}