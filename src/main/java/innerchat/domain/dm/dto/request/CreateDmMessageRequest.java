package innerchat.domain.dm.dto.request;

import jakarta.persistence.Lob;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateDmMessageRequest {

    Long workspaceId;
    Long dmRoomId;
    Long threadRootMessageId;
    Long userId;
    
    @Lob
    String content;
    String status;
    LocalDateTime createdAt;
}
