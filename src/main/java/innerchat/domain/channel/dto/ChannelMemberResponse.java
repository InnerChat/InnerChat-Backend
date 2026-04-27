package innerchat.domain.channel.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ChannelMemberResponse {
    private Long userId;
    private String userName;
}
