package horizon.SeRVe.core.controller;

import horizon.SeRVe.core.dto.sync.ChangedDocumentResponse;
import horizon.SeRVe.core.service.SyncService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sync")
@RequiredArgsConstructor
public class SyncController {

    private final SyncService syncService;

    @GetMapping("/documents")
    public ResponseEntity<List<ChangedDocumentResponse>> getChangedDocuments(
            @RequestParam String teamId,
            @RequestParam(defaultValue = "0") int lastSyncVersion) {

        List<ChangedDocumentResponse> changedDocuments =
                syncService.getChangedDocuments(teamId, lastSyncVersion);

        return ResponseEntity.ok(changedDocuments);
    }
}
