package innerchat.domain.dm.dto.response;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReadDmRoomListResponse {

    Long dmRoomId;
    List<String> participantNameList;
    Long unreadCount;

}
