package innerchat.domain.userStatus.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "user_login")
public class UserLogin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String loginId;

    private LocalDateTime loginAt;
    private LocalDateTime logoutAt;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserCurrentStatus currentStatus;

    public static UserLogin login(String loginId) {
        UserLogin loginHistory = new UserLogin();
        loginHistory.loginId = loginId;
        loginHistory.loginAt = LocalDateTime.now();
        loginHistory.currentStatus = UserCurrentStatus.ONLINE;
        return loginHistory;
    }

    public void logout() {
        this.logoutAt = LocalDateTime.now();
        this.currentStatus = UserCurrentStatus.OFFLINE;
    }

}
