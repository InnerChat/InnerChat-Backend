package innerchat.domain.dm.dto.response;

import innerchat.domain.common.entity.MessageStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DmMessageCreatedEvent {

    private String eventType;
    private Long dmRoomId;
    private Long dmMessageId;
    private Long authorId;
    private String authorName;
    private Long threadRootMessageId;
    private String content;
    private MessageStatus status;
    private LocalDateTime createdAt;
    private List<Long> reopenedUserIdList;
}
