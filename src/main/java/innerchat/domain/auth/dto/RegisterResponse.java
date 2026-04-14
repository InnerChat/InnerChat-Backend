package innerchat.domain.auth.dto;

import innerchat.domain.user.entity.UserRole;
import innerchat.domain.user.entity.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class RegisterResponse {

    private String loginId;
    private String userName;
    private UserRole role;
    private UserStatus status;
}
