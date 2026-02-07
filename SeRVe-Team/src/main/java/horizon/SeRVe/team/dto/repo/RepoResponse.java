package horizon.SeRVe.team.dto.repo;

import horizon.SeRVe.team.entity.RepoType;
import horizon.SeRVe.team.entity.Team;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RepoResponse {
    private String Teamid;
    private String name;
    private String description;
    private String type;
    private String ownerId;
    private String ownerEmail;

    // User JPA 참조 제거 → ownerEmail을 String으로 직접 받음
    public static RepoResponse of(Team team, String ownerEmail) {
        return RepoResponse.builder()
                .Teamid(team.getTeamId())
                .name(team.getName())
                .description(team.getDescription())
                .type(team.getType() != null ? team.getType().name() : RepoType.TEAM.name())
                .ownerId(team.getOwnerId())
                .ownerEmail(ownerEmail)
                .build();
    }
}
