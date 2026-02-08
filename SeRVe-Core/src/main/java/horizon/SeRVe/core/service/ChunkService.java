package horizon.SeRVe.core.service;

import horizon.SeRVe.common.dto.feign.MemberRoleResponse;
import horizon.SeRVe.common.dto.feign.UserInfoResponse;
import horizon.SeRVe.common.service.RateLimitService;
import horizon.SeRVe.core.dto.chunk.*;
import horizon.SeRVe.core.entity.*;
import horizon.SeRVe.core.feign.AuthServiceClient;
import horizon.SeRVe.core.feign.TeamServiceClient;
import horizon.SeRVe.core.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChunkService {

    private final VectorChunkRepository vectorChunkRepository;
    private final DocumentRepository documentRepository;
    private final TeamServiceClient teamServiceClient;
    private final AuthServiceClient authServiceClient;
    private final RateLimitService rateLimitService;

    @Transactional
    public void uploadChunks(String teamId, String fileName, String userId, ChunkUploadRequest request) {
        // 1. 팀 존재 확인
        if (!teamServiceClient.teamExists(teamId)) {
            throw new IllegalArgumentException("팀을 찾을 수 없습니다.");
        }

        // 1-1. Rate Limit 체크
        rateLimitService.checkAndRecordUpload(userId);

        // 2. 멤버십 및 권한 체크 (Federated Model: MEMBER 전용, ADMIN은 Key Master 역할만)
        MemberRoleResponse memberRole = teamServiceClient.getMemberRole(teamId, userId);

        // ADMIN은 업로드 금지 (Key Master 역할만 수행)
        if ("ADMIN".equals(memberRole.getRole())) {
            throw new SecurityException("ADMIN은 데이터 업로드가 불가능합니다. MEMBER만 업로드할 수 있습니다.");
        }

        // 3. Document 찾거나 생성
        Optional<Document> existingDoc = documentRepository.findByTeamIdAndOriginalFileName(teamId, fileName);
        Document document;

        if (existingDoc.isPresent()) {
            document = existingDoc.get();
            // 3-1. 기존 문서가 있으면 uploader 검증 (타인의 문서 수정 방지)
            if (!document.getUploaderId().equals(userId)) {
                throw new SecurityException("타인의 문서를 수정할 수 없습니다.");
            }
        } else {
            // 3-2. 새 문서 생성
            document = Document.builder()
                    .documentId(UUID.randomUUID().toString())
                    .teamId(teamId)
                    .uploaderId(userId)
                    .originalFileName(fileName)
                    .fileType("application/octet-stream")
                    .build();
            document = documentRepository.save(document);
        }

        // 4. 각 청크 처리 (UPDATE or INSERT)
        for (ChunkUploadItem item : request.getChunks()) {
            byte[] blobData = Base64.getDecoder().decode(item.getEncryptedBlob());

            Optional<VectorChunk> existingChunk = vectorChunkRepository
                    .findByDocumentIdAndChunkIndex(document.getDocumentId(), item.getChunkIndex());

            if (existingChunk.isPresent()) {
                // UPDATE: 기존 청크 내용 갱신 (version 자동 증가)
                VectorChunk chunk = existingChunk.get();
                chunk.updateContent(blobData);
                chunk.setDeleted(false);
            } else {
                // INSERT: 새 청크 생성 (version = 0)
                VectorChunk newChunk = VectorChunk.builder()
                        .chunkId(UUID.randomUUID().toString())
                        .documentId(document.getDocumentId())
                        .teamId(teamId)
                        .chunkIndex(item.getChunkIndex())
                        .encryptedBlob(blobData)
                        .isDeleted(false)
                        .build();
                vectorChunkRepository.save(newChunk);
            }
        }
    }

    @Transactional
    public void deleteChunk(String teamId, String fileName, int chunkIndex, String userId) {
        // 1. 팀 존재 확인
        if (!teamServiceClient.teamExists(teamId)) {
            throw new IllegalArgumentException("팀을 찾을 수 없습니다.");
        }

        // 2. ADMIN 권한 체크
        MemberRoleResponse memberRole = teamServiceClient.getMemberRole(teamId, userId);

        if (!"ADMIN".equals(memberRole.getRole())) {
            throw new SecurityException("청크 삭제는 ADMIN 권한이 필요합니다.");
        }

        // 3. Document 조회
        Document document = documentRepository.findByTeamIdAndOriginalFileName(teamId, fileName)
                .orElseThrow(() -> new IllegalArgumentException("문서를 찾을 수 없습니다."));

        // 4. 청크 논리적 삭제 (version 자동 증가)
        VectorChunk chunk = vectorChunkRepository
                .findByDocumentIdAndChunkIndex(document.getDocumentId(), chunkIndex)
                .orElseThrow(() -> new IllegalArgumentException("청크를 찾을 수 없습니다."));

        chunk.markAsDeleted();
    }

    @Transactional(readOnly = true)
    public List<ChunkSyncResponse> syncTeamChunks(String teamId, int lastVersion, String userId) {
        // 1. 팀 존재 확인
        if (!teamServiceClient.teamExists(teamId)) {
            throw new IllegalArgumentException("팀을 찾을 수 없습니다.");
        }

        // 2. 팀 멤버십 체크
        if (!teamServiceClient.memberExists(teamId, userId)) {
            throw new SecurityException("팀 멤버가 아닙니다.");
        }

        // 3. 팀의 모든 문서에서 변경된 청크 조회
        List<VectorChunk> chunks = vectorChunkRepository
                .findByTeamIdAndVersionGreaterThanOrderByVersionAsc(teamId, lastVersion);

        // 4. Document 정보 조회 (N+1 방지: IN 쿼리 사용)
        List<String> documentIds = chunks.stream()
                .map(VectorChunk::getDocumentId)
                .distinct()
                .collect(Collectors.toList());

        // Document ID → Uploader Email 매핑 생성
        java.util.Map<String, String> documentUploaderMap = documentRepository
                .findAllByDocumentIdIn(documentIds)
                .stream()
                .collect(Collectors.toMap(
                        Document::getDocumentId,
                        doc -> {
                            try {
                                UserInfoResponse userInfo = authServiceClient.getUserInfo(doc.getUploaderId());
                                return userInfo.getEmail();
                            } catch (Exception e) {
                                return "unknown";
                            }
                        }
                ));

        // 5. ChunkSyncResponse 생성 (createdBy 포함)
        return chunks.stream()
                .map(chunk -> {
                    String createdBy = documentUploaderMap.getOrDefault(chunk.getDocumentId(), "unknown");
                    return ChunkSyncResponse.from(chunk, createdBy);
                })
                .collect(Collectors.toList());
    }
}
