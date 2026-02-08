package horizon.SeRVe.core.dto.chunk;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChunkUploadRequest {
    private String fileName;
    private List<ChunkUploadItem> chunks;
}
