package innerchat.domain.dm.repository;

import innerchat.domain.dm.entity.DmPinnedMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DmPinnedMessageRepository extends JpaRepository<DmPinnedMessage, Long> {

}
