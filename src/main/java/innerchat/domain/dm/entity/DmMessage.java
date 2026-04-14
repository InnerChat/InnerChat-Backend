package innerchat.domain.dm.entity;

import innerchat.domain.common.entity.MessageStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "dm_messages")
public class DmMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long dmMessageId;

    @Column(nullable = false)
    private Long workspaceId;

    @Column(nullable = false)
    private Long dmRoomId;

    private Long threadRootMessageId;

    @Column(nullable = false)
    private Long authorId;

    @Lob
    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MessageStatus status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public DmMessage(Long workspaceId,
                     Long dmRoomId,
                     Long authorId,
                     String content) {
        this.workspaceId = workspaceId;
        this.dmRoomId = dmRoomId;
        this.authorId = authorId;
        this.content = content;
    }

    @PrePersist
    void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }

        if (status == null) {
            status = MessageStatus.NORMAL;
        }
    }
}
