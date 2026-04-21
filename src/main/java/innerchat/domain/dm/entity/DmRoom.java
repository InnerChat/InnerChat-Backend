package innerchat.domain.dm.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "dm_rooms")
public class DmRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long dmRoomId;

    @Column(nullable = false)
    private Long workspaceId;

    @Column(nullable = false, name = "room_type", length = 20)
    @Enumerated(EnumType.STRING)
    private DmRoomType dmRoomType;

    @Column(length = 64)
    private String dmPairKey;

    @Column(nullable = false)
    private Long lastMessageId;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public DmRoom(Long workspaceId,
                  DmRoomType dmRoomType,
                  String dmPairKey) {
        this.workspaceId = workspaceId;
        this.dmRoomType = dmRoomType;
        this.dmPairKey = dmPairKey;
    }

    @PrePersist
    void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (lastMessageId == null) {
            lastMessageId = 0L;
        }
    }
}
