package innerchat.domain.dm.entity;

import innerchat.domain.common.entity.MessageStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "dm_messages")
public class DmMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long dmMessageId;

    @Column(nullable = false)
    private Long dmRoomId;

    @Column
    private Long threadRootMessageId;

    @Column(nullable = false)
    private Long authorId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MessageStatus status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public DmMessage(Long dmRoomId,
                     Long authorId,
                     String content) {
        this.dmRoomId = dmRoomId;
        this.authorId = authorId;
        this.content = content;
    }

    public DmMessage(Long dmRoomId,
                     Long authorId,
                     String content,
                     Long threadRootMessageId) {
        this.dmRoomId = dmRoomId;
        this.authorId = authorId;
        this.content = content;
        this.threadRootMessageId = threadRootMessageId;
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
