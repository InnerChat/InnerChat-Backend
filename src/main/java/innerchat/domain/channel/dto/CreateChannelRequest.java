package innerchat.domain.channel.dto;

import innerchat.domain.channel.entity.ChannelType;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateChannelRequest {
    private String channelName;
    private String description;
    private ChannelType type;
}
