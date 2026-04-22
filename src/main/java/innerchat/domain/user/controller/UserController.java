package innerchat.domain.user.controller;

import innerchat.domain.user.dto.response.ReadUsersByWorkSpaceResponse;
import innerchat.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @GetMapping("/{workspaceId}")
    public ResponseEntity<List<ReadUsersByWorkSpaceResponse>> getUsersByWorkSpace(@PathVariable Long workspaceId) {
        return ResponseEntity.ok(userService.getUsersByWorkSpace(workspaceId));
    }

}
