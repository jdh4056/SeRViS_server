package horizon.SeRVe.team.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "edge_nodes")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class EdgeNode {

    @Id
    @Column(name = "node_id")
    private String nodeId;

    @Column(nullable = false, unique = true)
    private String serialNumber;

    @Column(nullable = false)
    private String hashedToken;

    @Column(name = "public_key", columnDefinition = "TEXT", nullable = false)
    private String publicKey;

    @Column(name = "encrypted_team_key", columnDefinition = "TEXT")
    private String encryptedTeamKey;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;
}
