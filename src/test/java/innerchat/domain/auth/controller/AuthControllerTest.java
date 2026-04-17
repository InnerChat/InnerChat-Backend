package innerchat.domain.auth.controller;

import innerchat.domain.auth.dto.RegisterRequest;
import innerchat.domain.auth.dto.RegisterResponse;
import innerchat.domain.auth.service.AuthService;
import innerchat.domain.user.entity.UserRole;
import innerchat.domain.user.entity.UserStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;
import tools.jackson.databind.ObjectMapper;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 실제 서버를 띄우지 않고 처리하기위해 MockMvc 사용
 */

@WebMvcTest(AuthController.class)
public class AuthControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @MockitoBean AuthService authService;

    public RegisterRequest newRegisterRequest() {
        return new RegisterRequest("ssm", "1", "신상민", UserRole.ADMIN);
    }

    //회원가입
    @Test
    void register_return200() throws Exception {
        RegisterRequest request = newRegisterRequest();
        RegisterResponse response = new RegisterResponse("ssm", "신상민", UserRole.ADMIN, UserStatus.ACTIVE);
        given(authService.register(any())).willReturn(response);

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk()) // 상태 코드
                .andExpect(jsonPath("$.loginId").value("ssm")) // loginId 체크
                .andExpect(jsonPath("$.userName").value("신상민")); // userName 체크

    }

    //등록 실패 400
    @Test
    void register_returnErr400() throws Exception {
        RegisterRequest request = newRegisterRequest();
        //given
        given(authService.register(any()))
                .willThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST));

        //when
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                //then
                .andExpect(status().isBadRequest());
    }

    //등록 실패 409
    @Test
    void register_returnErr409() throws Exception {
        RegisterRequest request = newRegisterRequest();
        //given
        given(authService.register(any()))
                .willThrow(new ResponseStatusException(HttpStatus.CONFLICT, "이미 사용 중인 아이디 입니다."));
        //when
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        //then
                .andExpect(status().isConflict());
    }

    //로그인 성공
    @Test
    void login_return200() throws Exception {

    }

}
