package horizon.SeRVe.team.service;

import horizon.SeRVe.common.dto.feign.UserInfoResponse;
import horizon.SeRVe.team.dto.member.InviteMemberRequest;
import horizon.SeRVe.team.dto.member.MemberKickResponse;
import horizon.SeRVe.team.dto.member.MemberResponse;
import horizon.SeRVe.team.dto.member.UpdateRoleRequest;
import horizon.SeRVe.team.dto.member.UpdateTeamKeysRequest;
import horizon.SeRVe.team.entity.*;
import horizon.SeRVe.team.feign.AuthServiceClient;
import horizon.SeRVe.team.repository.MemberRepository;
import horizon.SeRVe.team.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final TeamRepository teamRepository;
    private final AuthServiceClient authServiceClient;

    // 1. 멤버 초대
    @Transactional
    public void inviteMember(String teamId, String inviterUserId, InviteMemberRequest req) {
        Team team = teamRepository.findByTeamId(teamId)
                .orElseThrow(() -> new IllegalArgumentException("저장소가 없습니다."));

        // 초대자의 멤버십 및 권한 검증
        RepositoryMember inviterMember = memberRepository.findByTeamAndUserId(team, inviterUserId)
                .orElseThrow(() -> new SecurityException("초대자가 저장소 멤버가 아닙니다."));

        if (inviterMember.getRole() != Role.ADMIN) {
            throw new SecurityException("멤버 초대는 ADMIN 권한이 필요합니다.");
        }

        // 이메일로 유저 조회 (Auth 서비스 Feign 호출)
        UserInfoResponse inviteeInfo = authServiceClient.getUserByEmail(req.getEmail());
        String inviteeUserId = inviteeInfo.getUserId();

        // 중복 체크
        if (memberRepository.existsByTeamAndUserId(team, inviteeUserId)) {
            throw new IllegalArgumentException("이미 해당 저장소의 멤버입니다.");
        }

        // 멤버 추가
        RepositoryMemberId memberId = new RepositoryMemberId(team.getTeamId(), inviteeUserId);
        RepositoryMember newMember = RepositoryMember.builder()
                .id(memberId)
                .team(team)
                .role(Role.MEMBER)
                .encryptedTeamKey(req.getEncryptedTeamKey())
                .build();

        memberRepository.save(newMember);
    }

    // 2. 멤버 목록 조회
    public List<MemberResponse> getMembers(String teamId, String requesterId) {
        Team team = teamRepository.findByTeamId(teamId)
                .orElseThrow(() -> new IllegalArgumentException("저장소가 없습니다."));

        // 멤버십 검증
        if (!memberRepository.existsByTeamAndUserId(team, requesterId)) {
            throw new SecurityException("저장소 멤버가 아닙니다.");
        }

        return memberRepository.findAllByTeam(team).stream()
                .map(member -> {
                    String email;
                    try {
                        email = authServiceClient.getUserInfo(member.getUserId()).getEmail();
                    } catch (Exception e) {
                        email = "Unknown";
                    }
                    return MemberResponse.from(member, email);
                })
                .collect(Collectors.toList());
    }

    // 3. 멤버 강퇴
    @Transactional
    public MemberKickResponse kickMember(String teamId, String targetUserId, String adminUserId) {
        Team team = teamRepository.findByTeamId(teamId)
                .orElseThrow(() -> new IllegalArgumentException("저장소가 없습니다."));

        if (team.getOwnerId().equals(targetUserId)) {
            throw new SecurityException("저장소 소유자(Owner)는 강퇴할 수 없습니다.");
        }
        RepositoryMember targetMember = validateAdminAndGetTarget(teamId, targetUserId, adminUserId);
        memberRepository.delete(targetMember);

        return MemberKickResponse.createSuccess();
    }

    // 4. 권한 변경
    @Transactional
    public void updateMemberRole(String teamId, String targetUserId, String adminUserId, UpdateRoleRequest req) {
        RepositoryMember targetMember = validateAdminAndGetTarget(teamId, targetUserId, adminUserId);
        Role newRole;
        try {
            newRole = Role.valueOf(req.getRole().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("유효하지 않은 Role입니다.");
        }

        boolean isOwner = targetMember.getTeam().getOwnerId().equals(targetUserId);
        if (isOwner && newRole != Role.ADMIN) {
            throw new SecurityException("저장소 소유자(Owner)는 권한을 변경할 수 없습니다. (항상 ADMIN 유지)");
        }

        targetMember.setRole(newRole);
    }

    // 5. 키 로테이션
    @Transactional
    public void rotateTeamKeys(String teamId, String adminUserId, UpdateTeamKeysRequest req) {
        Team team = teamRepository.findByTeamId(teamId)
                .orElseThrow(() -> new IllegalArgumentException("저장소가 없습니다."));

        RepositoryMember adminMember = memberRepository.findByTeamAndUserId(team, adminUserId)
                .orElseThrow(() -> new SecurityException("관리자가 멤버가 아닙니다."));

        if (adminMember.getRole() != Role.ADMIN) {
            throw new SecurityException("관리자 권한이 필요합니다.");
        }

        for (UpdateTeamKeysRequest.MemberKey memberKey : req.getMemberKeys()) {
            RepositoryMember member = memberRepository.findByTeamAndUserId(team, memberKey.getUserId())
                    .orElseThrow(() -> new IllegalArgumentException("멤버를 찾을 수 없습니다: " + memberKey.getUserId()));

            member.setEncryptedTeamKey(memberKey.getEncryptedTeamKey());
        }
    }

    // [Helper] 권한 검증 및 타겟 조회
    private RepositoryMember validateAdminAndGetTarget(String teamId, String targetUserId, String adminUserId) {
        Team team = teamRepository.findByTeamId(teamId)
                .orElseThrow(() -> new IllegalArgumentException("저장소가 없습니다."));

        RepositoryMember adminMember = memberRepository.findByTeamAndUserId(team, adminUserId)
                .orElseThrow(() -> new SecurityException("관리자가 멤버가 아닙니다."));

        if (adminMember.getRole() != Role.ADMIN) {
            throw new SecurityException("관리자 권한이 필요합니다.");
        }

        return memberRepository.findByTeamAndUserId(team, targetUserId)
                .orElseThrow(() -> new IllegalArgumentException("대상 사용자가 멤버가 아닙니다."));
    }
}
