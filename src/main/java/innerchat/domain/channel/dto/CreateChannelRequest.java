package innerchat.domain.channel.dto;

import lombok.Getter;

@Getter
public class CreateChannelRequest {
    private String channelName;
    private String description;
}
