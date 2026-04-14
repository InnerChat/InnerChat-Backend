package innerchat.domain.auth.dto;

import innerchat.domain.user.entity.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

    private Long userId;
    private String userName;
    private UserRole role;

}
