package horizon.SeRVe.core.repository;

import horizon.SeRVe.core.entity.VectorChunk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VectorChunkRepository extends JpaRepository<VectorChunk, String> {

    List<VectorChunk> findByDocumentIdAndIsDeletedOrderByChunkIndexAsc(String documentId, boolean isDeleted);

    List<VectorChunk> findByDocumentIdAndVersionGreaterThanOrderByChunkIndexAsc(String documentId, int lastVersion);

    List<VectorChunk> findByTeamIdAndVersionGreaterThanOrderByVersionAsc(String teamId, int lastVersion);

    Optional<VectorChunk> findByDocumentIdAndChunkIndex(String documentId, int chunkIndex);

    List<VectorChunk> findByDocumentId(String documentId);
}
