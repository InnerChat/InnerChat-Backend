package innerchat.domain.dm.dto.response;

import innerchat.domain.common.entity.MessageStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ReadDmMessageResponse {

    private Long dmMessageId;
    private Long authorId;
    private String authorName;
    private String content;
    private MessageStatus status;
    private LocalDateTime createdAt;
}

