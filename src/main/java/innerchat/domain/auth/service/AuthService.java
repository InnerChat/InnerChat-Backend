package innerchat.domain.auth.service;

import innerchat.domain.auth.dto.LoginResponse;
import innerchat.domain.auth.dto.RegisterRequest;
import innerchat.domain.auth.dto.RegisterResponse;
import innerchat.domain.auth.session.SessionConst;
import innerchat.domain.user.entity.User;
import innerchat.domain.user.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        String loginId = request.getLoginId();
        String password = request.getPassword();
        String userName = request.getUserName();
        String role = request.getRole();
        String status = request.getStatus();

        if (loginId == null || loginId.isBlank() || password == null || password.isBlank() || userName == null || userName.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "loginId/password/userName은 필수입니다.");
        }
        if (userRepository.existsByLoginId(loginId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 사용 중인 아이디입니다.");
        }

        String encodedPassword = passwordEncoder.encode(password);
        User user = User.create(loginId, encodedPassword, userName, role, status);
        userRepository.save(user);
        return new RegisterResponse(user.getLoginId(), user.getUserName(), user.getRole(), user.getStatus());
    }

    @Transactional
    public LoginResponse login(String id, String password, HttpSession session) {
        if (id == null || id.isBlank() || password == null || password.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "id/password는 필수입니다.");
        }

        User user = userRepository.findByLoginId(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "아이디 또는 비밀번호가 올바르지 않습니다."));

        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "아이디 또는 비밀번호가 올바르지 않습니다.");
        }

        session.setAttribute(SessionConst.LOGIN_USER_ID, user.getId());
        return new LoginResponse(user.getId(), user.getLoginId(), user.getUserName());
    }

    @Transactional
    public void logout(HttpSession session) {
        session.invalidate();
    }
}
