package horizon.SeRVe.team.dto.member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberKickResponse {

    private boolean success;
    private boolean keyRotationRequired;
    private String message;
    private String keyRotationReason;

    public static MemberKickResponse createSuccess() {
        return MemberKickResponse.builder()
                .success(true)
                .keyRotationRequired(true)
                .message("멤버가 성공적으로 퇴출되었습니다.")
                .keyRotationReason("퇴출된 멤버는 여전히 팀 키를 보유하고 있습니다. 즉시 팀 키를 갱신하여 데이터 보안을 유지하세요.")
                .build();
    }
}
