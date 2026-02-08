package horizon.SeRVe.core.dto.chunk;

import horizon.SeRVe.core.entity.VectorChunk;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChunkSyncResponse {
    private String documentId;
    private String chunkId;
    private int chunkIndex;
    private byte[] encryptedBlob;
    private int version;
    private boolean isDeleted;
    private String createdBy;

    public static ChunkSyncResponse from(VectorChunk chunk) {
        return ChunkSyncResponse.builder()
                .documentId(chunk.getDocumentId())
                .chunkId(chunk.getChunkId())
                .chunkIndex(chunk.getChunkIndex())
                .encryptedBlob(chunk.getEncryptedBlob())
                .version(chunk.getVersion())
                .isDeleted(chunk.isDeleted())
                .createdBy(null)
                .build();
    }

    public static ChunkSyncResponse from(VectorChunk chunk, String createdBy) {
        return ChunkSyncResponse.builder()
                .documentId(chunk.getDocumentId())
                .chunkId(chunk.getChunkId())
                .chunkIndex(chunk.getChunkIndex())
                .encryptedBlob(chunk.getEncryptedBlob())
                .version(chunk.getVersion())
                .isDeleted(chunk.isDeleted())
                .createdBy(createdBy)
                .build();
    }
}
