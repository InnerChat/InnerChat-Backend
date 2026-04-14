package innerchat.domain.auth.controller;

import innerchat.domain.auth.dto.RegisterRequest;
import innerchat.domain.auth.dto.RegisterResponse;
import innerchat.domain.auth.service.AuthService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc //Mockmvc를 실제 컨텍스트와 연결
@Transactional
public class AuthControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    AuthService authService;

    private RegisterRequest defaultRegisterRequest () {
        return new RegisterRequest("ssm", "1", "신상민", "ADMIN", "ACTIVE");
    }

    //회원가입 성공 -> 200
    @Test
    void register_return200() throws Exception {
        RegisterRequest request = defaultRegisterRequest();

        ResultActions resultActions = mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.loginId").value("ssm"))
                .andExpect(jsonPath("$.userName").value("신상민"));
    }

    @Test
    void register_success() throws Exception {
        RegisterRequest request = defaultRegisterRequest();
        RegisterResponse register = authService.register(request);
        assertThat(register.getLoginId()).isEqualTo("ssm");
        assertThat(register.getUserName()).isEqualTo("신상민");
    }
}
