package horizon.SeRVe.core.repository;

import horizon.SeRVe.core.entity.Document;
import horizon.SeRVe.core.entity.EncryptedData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EncryptedDataRepository extends JpaRepository<EncryptedData, String> {
    Optional<EncryptedData> findByDocument(Document document);
}
