package innerchat.domain.userStatus.repository;

import innerchat.domain.userStatus.entity.UserLogin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserLoginRepository extends JpaRepository<UserLogin, Long> {



}
