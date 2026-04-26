package innerchat.domain.channel.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "channels")
public class Channel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long channelId;

    @Column(nullable = false)
    private Long workspaceId;

    @Column(nullable = false, length = 100)
    private String name;

    // ERD: description은 NOT NULL 없음 → nullable
    @Column(length = 500)
    private String description;

    @Column(nullable = false)
    private Long ownerId;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public Channel( String name, String description, Long ownerId) {
        this.name = name;
        this.description = description;
        this.ownerId = ownerId;
    }

    @PrePersist
    void prePersist() {

        if (workspaceId == null) {
            workspaceId = 1L;
        }
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
