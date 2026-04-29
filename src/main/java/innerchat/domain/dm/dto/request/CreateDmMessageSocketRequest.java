package innerchat.domain.dm.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateDmMessageSocketRequest {
    
    Long dmRoomId;
    Long threadRootMessageId;
    String content;
}
