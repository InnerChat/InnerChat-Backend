package innerchat.domain.user.repository;

import innerchat.domain.user.dto.response.ReadUsersByWorkSpaceResponse;

import java.util.List;

public interface UserRepositoryCustom {

    List<ReadUsersByWorkSpaceResponse> getUsersByWorkSpace(Long workspaceId);

}
