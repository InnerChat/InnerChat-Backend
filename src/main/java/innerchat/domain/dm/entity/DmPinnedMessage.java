package innerchat.domain.dm.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "dm_pinned_messages")
public class DmPinnedMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long dmPinMessageId;

    @Column(nullable = false)
    private Long dmRoomId;

    @Column(nullable = false)
    private Long dmMessageId;

    @Column(nullable = false)
    private Long pinnedBy;

    public DmPinnedMessage(Long dmRoomId, Long dmMessageId, Long pinnedBy) {
        this.dmRoomId = dmRoomId;
        this.dmMessageId = dmMessageId;
        this.pinnedBy = pinnedBy;
    }
}
