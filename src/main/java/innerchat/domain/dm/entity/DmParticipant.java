package innerchat.domain.dm.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
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

    @Column(nullable = false)
    private Boolean status;

    public DmParticipant(Long dmRoomId, Long userId) {
        this.dmRoomId = dmRoomId;
        this.userId = userId;
    }

    @PrePersist
    void prePersist() {
        if (joinendAt == null) {
            joinendAt = LocalDateTime.now();
        }
        if (lastReadMessageId == null) {
            lastReadMessageId = 0L;
        }
        if (status == null) {
            status = true;
        }
    }
}
