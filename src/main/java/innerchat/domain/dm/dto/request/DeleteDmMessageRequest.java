package innerchat.domain.dm.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DeleteDmMessageRequest {

    Long dmRoomId;
    Long dmMessageId;

}
