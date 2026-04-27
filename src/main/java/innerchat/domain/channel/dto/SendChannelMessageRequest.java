package innerchat.domain.channel.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SendChannelMessageRequest {
    private Long channelId;
    private Long threadRootMessageId;
    private String content;
}
