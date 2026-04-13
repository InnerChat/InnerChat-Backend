package innerchat.domain.auth.dto;

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
    private String role;
    private String status;
}
