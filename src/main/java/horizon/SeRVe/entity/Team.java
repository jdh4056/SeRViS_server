package horizon.SeRVe.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

// 기존: TeamRepository → Team (JPA Repository 인터페이스와 혼동 방지)
@Entity
@Table(name = "teams") // 기존: repositories
@Getter @Setter
@NoArgsConstructor
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name; // 저장소 이름 (예: "Project Alpha")

    @Column(length = 1000)
    private String description; // 설명

    private String ownerId; // 만든 사람 (User ID)

    private LocalDateTime createdAt = LocalDateTime.now();

    // [추가] DTO 지원을 위해 타입 필드 추가 (기본값 TEAM)
    @Enumerated(EnumType.STRING)
    private RepoType type = RepoType.TEAM;

    // 생성자 편의 메서드
    public Team(String name, String description, String ownerId) {
        this.name = name;
        this.description = description;
        this.ownerId = ownerId;
    }

    @Column(unique = true)
    private String teamId; // 기존: repoId

    public String getTeamId() { // 기존: getRepoId()
        return teamId;
    }
}
