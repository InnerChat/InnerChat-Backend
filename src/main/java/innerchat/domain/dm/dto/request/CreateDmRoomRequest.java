package innerchat.domain.dm.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateDmRoomRequest {

    Long workspaceId;
    List<Long> participantIdList;

}
