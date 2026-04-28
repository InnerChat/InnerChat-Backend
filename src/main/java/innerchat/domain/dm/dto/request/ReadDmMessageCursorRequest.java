package innerchat.domain.dm.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReadDmMessageCursorRequest {

    private Long cursor; //첫 호출시 lastMessageId
}

