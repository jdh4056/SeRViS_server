package horizon.SeRVe.team.repository;

import horizon.SeRVe.team.entity.RepositoryMember;
import horizon.SeRVe.team.entity.RepositoryMemberId;
import horizon.SeRVe.team.entity.Team;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<RepositoryMember, RepositoryMemberId> {

    // User JPA 참조 제거 → userId(String)로 조회
    @EntityGraph(attributePaths = {"team"})
    List<RepositoryMember> findAllByUserId(String userId);

    List<RepositoryMember> findAllByTeam(Team team);

    Optional<RepositoryMember> findByTeamAndUserId(Team team, String userId);

    boolean existsByTeamAndUserId(Team team, String userId);
}
