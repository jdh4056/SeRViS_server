package horizon.SeRVe.team.service;

import horizon.SeRVe.team.dto.member.UpdateRoleRequest;
import horizon.SeRVe.team.entity.*;
import horizon.SeRVe.team.feign.AuthServiceClient;
import horizon.SeRVe.team.repository.MemberRepository;
import horizon.SeRVe.team.repository.TeamRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @InjectMocks
    private MemberService memberService;

    @Mock private MemberRepository memberRepository;
    @Mock private TeamRepository teamRepository;
    @Mock private AuthServiceClient authServiceClient;

    @Test
    @DisplayName("보안 검증: 소유자(Owner)가 자신의 권한을 MEMBER로 내리려 하면 예외가 발생해야 한다")
    void updateRole_Fail_OwnerCannotDowngrade() {
        // 1. [상황 설정]
        String teamId = "team-1";
        String ownerId = "owner-user";

        Team mockTeam = new Team("My Team", "Desc", ownerId);
        mockTeam.setTeamId(teamId);

        RepositoryMember ownerMember = RepositoryMember.builder()
                .team(mockTeam)
                .role(Role.ADMIN)
                .build();

        // 2. [가짜 DB 동작 정의]
        given(teamRepository.findByTeamId(teamId)).willReturn(Optional.of(mockTeam));
        given(memberRepository.findByTeamAndUserId(mockTeam, ownerId)).willReturn(Optional.of(ownerMember));

        // 3. [실행 및 검증]
        UpdateRoleRequest request = new UpdateRoleRequest("MEMBER");

        SecurityException exception = assertThrows(SecurityException.class, () -> {
            memberService.updateMemberRole(teamId, ownerId, ownerId, request);
        });

        // 4. [메시지 확인]
        assertEquals("저장소 소유자(Owner)는 권한을 변경할 수 없습니다. (항상 ADMIN 유지)", exception.getMessage());
    }

    @Test
    @DisplayName("보안 검증: 누군가 소유자(Owner)를 강퇴하려 하면 예외가 발생해야 한다")
    void kickMember_Fail_CannotKickOwner() {
        // 1. [상황 설정]
        String teamId = "team-1";
        String ownerId = "owner-user";
        String adminId = "another-admin";

        Team mockTeam = new Team("My Team", "Desc", ownerId);
        mockTeam.setTeamId(teamId);

        // 2. [가짜 DB 동작 정의]
        given(teamRepository.findByTeamId(teamId)).willReturn(Optional.of(mockTeam));

        // 3. [실행 및 검증]
        SecurityException exception = assertThrows(SecurityException.class, () -> {
            memberService.kickMember(teamId, ownerId, adminId);
        });

        assertEquals("저장소 소유자(Owner)는 강퇴할 수 없습니다.", exception.getMessage());
    }
}
