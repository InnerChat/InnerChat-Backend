package innerchat.domain.dm.repository;

import innerchat.domain.dm.entity.DmParticipant;

public interface DmParticipantRepositoryCustom {

    DmParticipant findByUserIdAndDmRoomId(Long userId, Long dmRoomId);
}

