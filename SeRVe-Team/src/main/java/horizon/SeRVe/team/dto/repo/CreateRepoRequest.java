package horizon.SeRVe.team.dto.repo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateRepoRequest {
    private String name;
    private String description;
    private String encryptedTeamKey;
}
