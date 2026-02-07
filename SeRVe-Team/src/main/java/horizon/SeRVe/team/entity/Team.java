package horizon.SeRVe.team.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.UUID;

import java.time.LocalDateTime;

@Entity
@Table(name = "teams")
@Getter @Setter
@NoArgsConstructor
public class Team {

    @Id
    @Column(name = "team_id", nullable = false, unique = true)
    private String teamId;

    @Column(nullable = false)
    private String name;

    @Column(length = 1000)
    private String description;

    private String ownerId;

    private LocalDateTime createdAt = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    private RepoType type = RepoType.TEAM;

    public Team(String name, String description, String ownerId) {
        this.name = name;
        this.description = description;
        this.ownerId = ownerId;
        this.teamId = UUID.randomUUID().toString();
    }
}
