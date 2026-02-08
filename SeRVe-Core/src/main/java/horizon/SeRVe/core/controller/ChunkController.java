package horizon.SeRVe.core.controller;

import horizon.SeRVe.core.dto.chunk.ChunkSyncResponse;
import horizon.SeRVe.core.dto.chunk.ChunkUploadRequest;
import horizon.SeRVe.core.service.ChunkService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ChunkController {

    private final ChunkService chunkService;

    @PostMapping("/api/teams/{teamId}/chunks")
    public ResponseEntity<Void> uploadChunks(
            @PathVariable String teamId,
            Authentication authentication,
            @RequestBody ChunkUploadRequest request) {

        String userId = (String) authentication.getPrincipal();
        chunkService.uploadChunks(teamId, request.getFileName(), userId, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/api/teams/{teamId}/chunks/{chunkIndex}")
    public ResponseEntity<Void> deleteChunk(
            @PathVariable String teamId,
            @PathVariable int chunkIndex,
            @RequestParam String fileName,
            Authentication authentication) {

        String userId = (String) authentication.getPrincipal();
        chunkService.deleteChunk(teamId, fileName, chunkIndex, userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/api/sync/chunks")
    public ResponseEntity<List<ChunkSyncResponse>> syncTeamChunks(
            @RequestParam String teamId,
            @RequestParam(defaultValue = "0") int lastVersion,
            Authentication authentication) {

        String userId = (String) authentication.getPrincipal();
        List<ChunkSyncResponse> response = chunkService.syncTeamChunks(
                teamId, lastVersion, userId);
        return ResponseEntity.ok(response);
    }
}
