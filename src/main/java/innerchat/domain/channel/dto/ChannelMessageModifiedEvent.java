package innerchat.domain.channel.dto;

import innerchat.domain.channel.entity.ChannelMessageStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ChannelMessageModifiedEvent {
    private Long channelMessageId;
    private Long channelId;
    private String content;
    private ChannelMessageStatus status;
}
