package horizon.SeRVe.team.controller;

import horizon.SeRVe.common.dto.feign.EdgeNodeAuthResponse;
import horizon.SeRVe.common.dto.feign.MemberRoleResponse;
import horizon.SeRVe.team.entity.EdgeNode;
import horizon.SeRVe.team.entity.RepositoryMember;
import horizon.SeRVe.team.entity.Team;
import horizon.SeRVe.team.repository.EdgeNodeRepository;
import horizon.SeRVe.team.repository.MemberRepository;
import horizon.SeRVe.team.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 서비스 간 통신 전용 내부 API.
 * Auth/Core 서비스가 Feign으로 호출합니다.
 * SecurityConfig에서 /internal/** 경로는 인증 없이 허용됩니다.
 */
@RestController
@RequestMapping("/internal")
@RequiredArgsConstructor
public class InternalTeamController {

    private final TeamRepository teamRepository;
    private final MemberRepository memberRepository;
    private final EdgeNodeRepository edgeNodeRepository;

    // 팀 존재 여부 확인
    @GetMapping("/teams/{teamId}/exists")
    public ResponseEntity<Boolean> teamExists(@PathVariable String teamId) {
        return ResponseEntity.ok(teamRepository.findByTeamId(teamId).isPresent());
    }

    // 멤버 역할 조회
    @GetMapping("/teams/{teamId}/members/{userId}/role")
    public ResponseEntity<MemberRoleResponse> getMemberRole(
            @PathVariable String teamId,
            @PathVariable String userId) {
        Team team = teamRepository.findByTeamId(teamId)
                .orElseThrow(() -> new IllegalArgumentException("팀을 찾을 수 없습니다."));

        RepositoryMember member = memberRepository.findByTeamAndUserId(team, userId)
                .orElseThrow(() -> new IllegalArgumentException("멤버를 찾을 수 없습니다."));

        return ResponseEntity.ok(MemberRoleResponse.builder()
                .userId(member.getUserId())
                .teamId(teamId)
                .role(member.getRole().name())
                .encryptedTeamKey(member.getEncryptedTeamKey())
                .build());
    }

    // 멤버 존재 여부 확인
    @GetMapping("/teams/{teamId}/members/{userId}/exists")
    public ResponseEntity<Boolean> memberExists(
            @PathVariable String teamId,
            @PathVariable String userId) {
        Team team = teamRepository.findByTeamId(teamId)
                .orElseThrow(() -> new IllegalArgumentException("팀을 찾을 수 없습니다."));
        return ResponseEntity.ok(memberRepository.existsByTeamAndUserId(team, userId));
    }

    // EdgeNode 시리얼 번호로 조회 (Auth 서비스의 로봇 로그인에서 사용)
    @GetMapping("/edge-nodes/by-serial/{serialNumber}")
    public ResponseEntity<EdgeNodeAuthResponse> getEdgeNodeBySerial(@PathVariable String serialNumber) {
        EdgeNode edgeNode = edgeNodeRepository.findBySerialNumber(serialNumber)
                .orElseThrow(() -> new IllegalArgumentException("등록되지 않은 기기입니다."));

        return ResponseEntity.ok(EdgeNodeAuthResponse.builder()
                .nodeId(edgeNode.getNodeId())
                .serialNumber(edgeNode.getSerialNumber())
                .hashedToken(edgeNode.getHashedToken())
                .publicKey(edgeNode.getPublicKey())
                .encryptedTeamKey(edgeNode.getEncryptedTeamKey())
                .teamId(edgeNode.getTeam().getTeamId())
                .build());
    }

    // EdgeNode의 팀 ID 조회 (Core 서비스에서 사용)
    @GetMapping("/edge-nodes/{nodeId}/team-id")
    public ResponseEntity<String> getEdgeNodeTeamId(@PathVariable String nodeId) {
        EdgeNode edgeNode = edgeNodeRepository.findById(nodeId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 로봇입니다."));
        return ResponseEntity.ok(edgeNode.getTeam().getTeamId());
    }
}
