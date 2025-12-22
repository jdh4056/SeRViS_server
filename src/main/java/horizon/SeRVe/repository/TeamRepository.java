package horizon.SeRVe.repository;

import horizon.SeRVe.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

// 기존: TeamRepoRepository → TeamRepository
public interface TeamRepository extends JpaRepository<Team, Long> {
    Optional<Team> findByName(String name);

    /**
     * [추가] 내가 소유자(Owner)로 있는 저장소 목록 조회
     * 기존 TeamRepository 엔티티의 String ownerId 필드를 활용.
     */
    List<Team> findAllByOwnerId(String ownerId);

    // teamId로 단건 조회 (String 타입 식별자 사용 시)
    // 기존: findByRepoId → findByTeamId
    Optional<Team> findByTeamId(String teamId);
}
