package innerchat.domain.dm.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "dm_participants")
public class DmParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long dmParticipantId;

    @Column(nullable = false)
    private Long dmRoomId;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private LocalDateTime joinendAt;

    @Column(nullable = false)
    private Long lastReadMessageId;

    public DmParticipant(Long dmRoomId, Long userId) {
        this.dmRoomId = dmRoomId;
        this.userId = userId;
    }

    @PrePersist
    void prePersist() {
        if (joinendAt == null) {
            joinendAt = LocalDateTime.now();
        }
    }
}
