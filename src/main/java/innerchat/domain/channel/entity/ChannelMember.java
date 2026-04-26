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
@Table(name = "channel_members")
public class ChannelMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long channelMemberId;

    @Column(nullable = false)
    private Long channelId;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private LocalDateTime joinedAt;

    @Column(nullable = false)
    private LocalDateTime lastReadAt;

    public ChannelMember(Long channelId, Long userId) {
        this.channelId = channelId;
        this.userId = userId;
    }

    @PrePersist
    void prePersist() {
        if (joinedAt == null) {
            joinedAt = LocalDateTime.now();
        }
        // 최초 가입 시 lastReadAt을 joinedAt과 동일하게 초기화한다.
        // 이후 메시지 조회 시 업데이트된다.
        if (lastReadAt == null) {
            lastReadAt = joinedAt;
        }
    }
}
