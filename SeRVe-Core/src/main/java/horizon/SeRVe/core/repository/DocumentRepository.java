package horizon.SeRVe.core.repository;

import horizon.SeRVe.core.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {

    List<Document> findAllByTeamId(String teamId);

    Optional<Document> findByDocumentId(String documentId);

    Optional<Document> findByTeamIdAndOriginalFileName(String teamId, String originalFileName);

    List<Document> findAllByDocumentIdIn(List<String> documentIds);
}
