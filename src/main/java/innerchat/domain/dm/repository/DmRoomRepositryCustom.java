package innerchat.domain.dm.repository;

import innerchat.domain.dm.entity.DmRoomType;

import java.util.Optional;

public interface DmRoomRepositryCustom {

    Optional<Long> findDmRoomIdByTypeAndPairKey(DmRoomType roomType, String pairKey);
}
