package innerchat.domain.user.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(nullable = false)
    private Long workspaceId;

    @Column(nullable = false, unique = true, length = 50)
    private String loginId;

    @Column(nullable = false)
    private String passwordHash;

    @Column(nullable = false, length = 50)
    private String userName;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private UserStatus status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public User(Long workspaceId,
                String loginId,
                String passwordHash,
                String userName,
                UserRole role) {
        this.workspaceId = workspaceId;
        this.loginId = loginId;
        this.passwordHash = passwordHash;
        this.userName = userName;
        this.role = role;
    }

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }

        if (status == null) {
            status = UserStatus.ACTIVE;
        }
    }

}
