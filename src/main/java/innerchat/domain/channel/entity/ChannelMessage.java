package innerchat.domain.channel.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "channel_messages")
public class ChannelMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long channelMessageId;

    @Column(nullable = false)
    private Long channelId;

    // 스레드 루트 메시지 ID. 일반 메시지는 null, 스레드 답글은 루트 메시지 ID를 가진다.
    private Long threadRootMessageId;

    @Column(nullable = false)
    private Long authorId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false, length = 20)
    private String status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public ChannelMessage(Long channelId, Long authorId, String content, String status) {
        this.channelId = channelId;
        this.authorId = authorId;
        this.content = content;
        this.status = status;
    }

    @PrePersist
    void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
