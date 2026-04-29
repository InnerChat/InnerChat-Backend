package innerchat.domain.channel.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ChannelMemberEvent {
    public enum EventType {
        MEMBER_JOINED, // 직접 참가
        MEMBER_INVITED, //초대로 추가
        MEMBER_LEFT //나가기
    }

    private EventType eventType;
    private Long channelId;
    private Long userId;
    private String userName;
}
