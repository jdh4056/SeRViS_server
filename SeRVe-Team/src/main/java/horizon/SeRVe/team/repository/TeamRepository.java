package horizon.SeRVe.team.repository;

import horizon.SeRVe.team.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TeamRepository extends JpaRepository<Team, Long> {
    Optional<Team> findByName(String name);
    List<Team> findAllByOwnerId(String ownerId);
    Optional<Team> findByTeamId(String teamId);
}
