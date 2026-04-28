package innerchat.domain.dm.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DmInboxEvent {

    private String eventType;
    private Long dmRoomId;
    private Long dmMessageId;
    private Long authorId;
}
