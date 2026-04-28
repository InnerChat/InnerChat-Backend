package innerchat.domain.dm.repository;

import innerchat.domain.dm.entity.DmMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DmMessageRepository extends JpaRepository<DmMessage, Long>, DmMessageRepositoryCustom {
    boolean existsByDmMessageIdAndDmRoomId(Long dmMessageId, Long dmRoomId);
}
