package innerchat.domain.channel.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ChannelMessageCreatedEvent {
    private Long channelMessageId;
    private Long channelId;
    private Long authorId;
    private String authorName;
    private String content;
    private String status;
    private LocalDateTime createdAt;
}
