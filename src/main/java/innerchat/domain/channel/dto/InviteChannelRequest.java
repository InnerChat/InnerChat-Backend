package innerchat.domain.channel.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class InviteChannelRequest {
    private Long targetUserId;
}
