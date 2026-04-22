package innerchat.domain.user.dto.response;

import innerchat.domain.user.entity.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReadUsersByWorkSpaceResponse {

    private Long userId;
    private String userName;
    private UserRole role;

}
