package innerchat.domain.dm.repository;

import innerchat.domain.dm.entity.DmRoom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DmRoomRepositry extends JpaRepository<DmRoom, Long>, DmRoomRepositryCustom {


}
