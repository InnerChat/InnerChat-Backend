package innerchat.domain.dm.repository;

import innerchat.domain.dm.entity.DmRoom;
import innerchat.domain.dm.entity.DmRoomType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DmRoomRepositry extends JpaRepository<DmRoom, Long>, DmRoomRepositryCustom {

    List<DmRoom> findByWorkspaceId(Long workspaceId);

    Optional<DmRoom> findByWorkspaceIdAndDmRoomTypeAndDmPairKey(
            Long workspaceId,
            DmRoomType dmRoomType,
            String dmPairKey
    );
}
