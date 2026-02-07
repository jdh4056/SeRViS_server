package horizon.SeRVe.team.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RepositoryMemberId implements Serializable {
    private String teamId;
    private String userId;
}
