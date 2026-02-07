package horizon.SeRVe.team.controller;

import horizon.SeRVe.team.dto.member.InviteMemberRequest;
import horizon.SeRVe.team.dto.member.MemberKickResponse;
import horizon.SeRVe.team.dto.member.MemberResponse;
import horizon.SeRVe.team.dto.member.UpdateRoleRequest;
import horizon.SeRVe.team.dto.member.UpdateTeamKeysRequest;
import horizon.SeRVe.team.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teams/{teamId}/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    // 1. 멤버 초대 (ADMIN 권한 필요)
    @PostMapping
    public ResponseEntity<Void> inviteMember(
            @PathVariable String teamId,
            Authentication authentication,
            @RequestBody InviteMemberRequest request) {
        String userId = (String) authentication.getPrincipal();
        memberService.inviteMember(teamId, userId, request);
        return ResponseEntity.ok().build();
    }

    // 2. 멤버 목록 조회
    @GetMapping
    public ResponseEntity<List<MemberResponse>> getMembers(
            @PathVariable String teamId,
            Authentication authentication) {
        String userId = (String) authentication.getPrincipal();
        List<MemberResponse> members = memberService.getMembers(teamId, userId);
        return ResponseEntity.ok(members);
    }

    // 3. 멤버 강퇴 (ADMIN 권한 필요)
    @DeleteMapping("/{targetUserId}")
    public ResponseEntity<MemberKickResponse> kickMember(
            @PathVariable String teamId,
            @PathVariable String targetUserId,
            Authentication authentication) {
        String userId = (String) authentication.getPrincipal();
        MemberKickResponse response = memberService.kickMember(teamId, targetUserId, userId);
        return ResponseEntity.ok(response);
    }

    // 4. 권한 변경 (ADMIN 권한 필요)
    @PutMapping("/{targetUserId}")
    public ResponseEntity<Void> updateMemberRole(
            @PathVariable String teamId,
            @PathVariable String targetUserId,
            Authentication authentication,
            @RequestBody UpdateRoleRequest request) {
        String userId = (String) authentication.getPrincipal();
        memberService.updateMemberRole(teamId, targetUserId, userId, request);
        return ResponseEntity.ok().build();
    }

    // 5. 키 로테이션 (팀 키 일괄 업데이트)
    @PostMapping("/rotate-keys")
    public ResponseEntity<Void> rotateTeamKeys(
            @PathVariable String teamId,
            Authentication authentication,
            @RequestBody UpdateTeamKeysRequest request) {
        String userId = (String) authentication.getPrincipal();
        memberService.rotateTeamKeys(teamId, userId, request);
        return ResponseEntity.ok().build();
    }
}
