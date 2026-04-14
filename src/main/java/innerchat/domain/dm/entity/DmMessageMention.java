package innerchat.domain.dm.entity;

import innerchat.domain.common.entity.MentionType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "dm_message_mentions")
public class DmMessageMention {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long dmMessageMentionId;

    @Column(nullable = false)
    private Long dmMessageId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MentionType mentionType;

    private Long mentionUserId;

    private Long mentionChannelId;
}
