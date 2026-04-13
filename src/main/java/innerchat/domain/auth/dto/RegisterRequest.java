package innerchat.domain.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    private String loginId;
    private String password;
    private String userName;
    private String role;
    private String status;
}
