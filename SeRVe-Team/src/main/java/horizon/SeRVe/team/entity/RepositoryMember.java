package horizon.SeRVe.team.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "repository_members")
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class RepositoryMember {

    @EmbeddedId
    private RepositoryMemberId id;

    @MapsId("teamId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    // User JPA 참조 제거 (Auth DB에 있으므로 Feign으로 조회)
    // userId는 EmbeddedId(RepositoryMemberId.userId)를 통해 DB에 매핑됨
    @Column(name = "user_id", insertable = false, updatable = false)
    private String userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(name = "encrypted_team_key", columnDefinition = "TEXT", nullable = false)
    private String encryptedTeamKey;
}
