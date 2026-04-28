package innerchat.domain.dm.dto;

public interface DmRoomListNativeProjection {

    Long getDmRoomId();

    String getParticipantNameListRaw();

    String getDmRoomType();

    Long getLastMessageId();

    Long getUnreadCount();
}
