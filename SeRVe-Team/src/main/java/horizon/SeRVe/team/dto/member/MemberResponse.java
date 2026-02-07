package horizon.SeRVe.team.dto.member;

import horizon.SeRVe.team.entity.RepositoryMember;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberResponse {

    private String userId;
    private String email;
    private String role;

    // User JPA 참조 제거 → email을 외부에서 주입받음
    public static MemberResponse from(RepositoryMember member, String email) {
        return MemberResponse.builder()
                .userId(member.getUserId())
                .email(email)
                .role(member.getRole().name())
                .build();
    }
}
