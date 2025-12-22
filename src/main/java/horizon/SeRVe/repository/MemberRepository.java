package horizon.SeRVe.repository;

import horizon.SeRVe.entity.RepositoryMember;
import horizon.SeRVe.entity.RepositoryMemberId;
import horizon.SeRVe.entity.Team;
import horizon.SeRVe.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<RepositoryMember, RepositoryMemberId> {

    /**
     * 1. 내가 속한 모든 저장소 조회 (Join 최적화)
     * 멤버 정보 조회 시 연관된 team 정보를 함께 가져옵니다.
     */
    @EntityGraph(attributePaths = {"team"}) // 기존: teamRepository
    List<RepositoryMember> findAllByUser(User user);

    /**
     * 2. 특정 저장소의 멤버 목록 조회 (Join 최적화)
     * 멤버 목록 조회 시 연관된 User 정보를 함께 가져옵니다 (이메일, ID 등).
     */
    @EntityGraph(attributePaths = {"user"})
    List<RepositoryMember> findAllByTeam(Team team); // 기존: findAllByTeamRepository

    /**
     * 3. 특정 저장소에서 특정 유저의 멤버 정보 확인 (권한 체크용)
     */
    // 기존: findByTeamRepositoryAndUser → findByTeamAndUser
    Optional<RepositoryMember> findByTeamAndUser(Team team, User user);

    /**
     * 4. 이미 해당 저장소의 멤버인지 확인 (중복 초대 방지)
     */
    // 기존: existsByTeamRepositoryAndUser → existsByTeamAndUser
    boolean existsByTeamAndUser(Team team, User user);
}
