package horizon.SeRVe.core.service;

import horizon.SeRVe.core.dto.sync.ChangedDocumentResponse;
import horizon.SeRVe.core.entity.Document;
import horizon.SeRVe.core.feign.TeamServiceClient;
import horizon.SeRVe.core.repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SyncService {

    private final DocumentRepository documentRepository;
    private final TeamServiceClient teamServiceClient;

    @Transactional(readOnly = true)
    public List<ChangedDocumentResponse> getChangedDocuments(String teamId, int lastSyncVersion) {
        if (!teamServiceClient.teamExists(teamId)) {
            throw new IllegalArgumentException("존재하지 않는 팀입니다.");
        }

        // 팀의 모든 문서 조회
        List<Document> allDocuments = documentRepository.findAllByTeamId(teamId);

        // 버전 필터링 (version > lastSyncVersion)
        return allDocuments.stream()
                .filter(doc -> doc.getEncryptedData() != null &&
                        doc.getEncryptedData().getVersion() > lastSyncVersion)
                .map(ChangedDocumentResponse::from)
                .collect(Collectors.toList());
    }
}
