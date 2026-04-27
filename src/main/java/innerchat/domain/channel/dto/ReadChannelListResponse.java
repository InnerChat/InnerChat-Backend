package innerchat.domain.channel.dto;

import innerchat.domain.channel.entity.ChannelType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReadChannelListResponse {
    private Long channelId;
    private String name;
    private String description;
    private ChannelType type;
    private Long ownerId;
    private Long memberCount;
    private boolean isMember;
}
