package horizon.SeRVe.team.service;

import horizon.SeRVe.team.dto.edge.RegisterEdgeNodeRequest;
import horizon.SeRVe.team.entity.EdgeNode;
import horizon.SeRVe.team.entity.Team;
import horizon.SeRVe.team.repository.EdgeNodeRepository;
import horizon.SeRVe.team.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EdgeNodeService {

    private final EdgeNodeRepository edgeNodeRepository;
    private final TeamRepository teamRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public String registerEdgeNode(RegisterEdgeNodeRequest request) {
        if (edgeNodeRepository.findBySerialNumber(request.getSerialNumber()).isPresent()) {
            throw new IllegalArgumentException("이미 등록된 시리얼 번호입니다.");
        }

        Team team = teamRepository.findByTeamId(request.getTeamId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 팀입니다."));

        String hashedToken = passwordEncoder.encode(request.getApiToken());

        EdgeNode edgeNode = EdgeNode.builder()
                .nodeId(UUID.randomUUID().toString())
                .serialNumber(request.getSerialNumber())
                .hashedToken(hashedToken)
                .publicKey(request.getPublicKey())
                .encryptedTeamKey(request.getEncryptedTeamKey())
                .team(team)
                .build();

        edgeNodeRepository.save(edgeNode);

        return edgeNode.getNodeId();
    }

    @Transactional(readOnly = true)
    public String getTeamKey(String nodeId) {
        EdgeNode edgeNode = edgeNodeRepository.findById(nodeId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 로봇입니다."));

        if (edgeNode.getEncryptedTeamKey() == null) {
            throw new IllegalStateException("팀 키가 설정되지 않았습니다.");
        }

        return edgeNode.getEncryptedTeamKey();
    }
}
