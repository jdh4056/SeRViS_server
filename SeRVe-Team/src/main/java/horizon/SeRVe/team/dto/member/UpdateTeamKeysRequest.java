package horizon.SeRVe.team.dto.member;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTeamKeysRequest {

    private List<MemberKey> memberKeys;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MemberKey {
        private String userId;
        private String encryptedTeamKey;
    }
}
