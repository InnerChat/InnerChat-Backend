package innerchat.domain.dm.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ReadDmMessageCursorResponse {

    private List<ReadDmMessageResponse> messages;
    private Long nextCursor;   // -1 일시 끝값
    private boolean hasNext;
}

