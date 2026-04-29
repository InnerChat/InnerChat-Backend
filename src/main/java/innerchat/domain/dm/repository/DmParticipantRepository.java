package innerchat.domain.dm.repository;

import innerchat.domain.dm.entity.DmParticipant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DmParticipantRepository extends JpaRepository<DmParticipant, Long>, DmParticipantRepositoryCustom {

    boolean existsByDmRoomIdAndUserIdAndStatusTrue(Long dmRoomId, Long userId);

    List<DmParticipant> findAllByDmRoomId(Long dmRoomId);
    
}
