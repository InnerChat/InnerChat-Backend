package innerchat.domain.dm.service;

import innerchat.domain.dm.dto.response.ReadDmRoomListResponse;
import innerchat.domain.dm.repository.DmRoomRepositry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DmRoomService {

    private final DmRoomRepositry dmRoomRepositry;

    public List<ReadDmRoomListResponse> getDmRoomList(Long userId) {
        return dmRoomRepositry.getDmRoomList(userId);
    }
    
}