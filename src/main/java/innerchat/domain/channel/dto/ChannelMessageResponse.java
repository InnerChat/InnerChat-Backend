package innerchat.domain.channel.dto;

import innerchat.domain.channel.entity.ChannelMessage;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ChannelMessageResponse {
    private final Long channelMessageId;
    private final Long channelId;
    private final Long authorId;
    private final String authorName;
    private final String content;
    private final String status;
    private final LocalDateTime createdAt;

    public ChannelMessageResponse(ChannelMessage msg, String authorName) {
        this.channelMessageId = msg.getChannelMessageId();
        this.channelId        = msg.getChannelId();
        this.authorId         = msg.getAuthorId();
        this.authorName       = authorName;
        this.content          = msg.getContent();
        this.status           = msg.getStatus().name();
        this.createdAt        = msg.getCreatedAt();
    }

}
