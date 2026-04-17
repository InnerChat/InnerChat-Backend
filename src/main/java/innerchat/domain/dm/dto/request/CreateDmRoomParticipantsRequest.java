package innerchat.domain.dm.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateDmRoomParticipantsRequest {

    Long dmRoomId;
    List<Long> userIdList;
    
}
