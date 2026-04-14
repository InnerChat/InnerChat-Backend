package innerchat.domain.auth.service;

import innerchat.domain.auth.dto.LoginResponse;
import innerchat.domain.auth.dto.RegisterRequest;
import innerchat.domain.auth.dto.RegisterResponse;
import innerchat.domain.auth.session.SessionConst;
import innerchat.domain.user.entity.User;
import innerchat.domain.user.entity.UserRole;
import innerchat.domain.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@SpringBootTest
@Transactional
@Slf4j
class AuthServiceTest {

    @Autowired UserRepository userRepository;
    @Autowired PasswordEncoder passwordEncoder;
    @Autowired AuthService authService;

    private RegisterRequest defaultRegisterRequest () {
        return new RegisterRequest("ssm1", "1", "신상민", UserRole.ADMIN);
    }

    // 등록
    @Test
    void register_success() {
        // given
        RegisterRequest request = defaultRegisterRequest();
//        given(userRepository.existsByLoginId("ssm")).willReturn(false);
//        given(passwordEncoder.encode("1")).willReturn("encoded_1");
//        given(userRepository.save(any())).willAnswer(inv -> inv.getArgument(0));

        // when
        RegisterResponse response = authService.register(request);

        // then
        assertThat(response.getLoginId()).isEqualTo("ssm");
        assertThat(response.getUserName()).isEqualTo("신상민");
    }

    //로그인 성공
    @Test
    void login_success() {
        //given
        authService.register(defaultRegisterRequest());
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();

        //when
        LoginResponse loginResponse = authService.login("ssm", "1");

        //then
        assertThat(loginResponse.getUserName()).isEqualTo("신상민"); //user 이름 검증
        assertThat(mockRequest.getSession(false)).isNotNull(); // session 여부 체크
        assertThat(mockRequest.getSession(false).getAttribute(SessionConst.LOGIN_USER_ID))
                .isEqualTo(loginResponse.getUserId()); // 세션의 로그인 userId와 로그인 응답객체 userId 검증
    }



}