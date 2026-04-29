package innerchat.domain.channel.dto;

import innerchat.domain.channel.entity.ChannelMessageStatus;
import lombok.Getter;

@Getter
public class MessageModifyRequest {
    private Long channelMessageId;
    private String content;
    private ChannelMessageStatus action;
}
