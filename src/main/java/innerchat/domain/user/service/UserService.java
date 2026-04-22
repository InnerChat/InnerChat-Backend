package innerchat.domain.user.service;

import innerchat.domain.user.dto.response.ReadUsersByWorkSpaceResponse;
import innerchat.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public List<ReadUsersByWorkSpaceResponse> getUsersByWorkSpace(Long workspaceId) {
        return userRepository.getUsersByWorkSpace(workspaceId);
    }
}
