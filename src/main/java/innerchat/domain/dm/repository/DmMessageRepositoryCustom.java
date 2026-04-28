package innerchat.domain.dm.repository;

import innerchat.domain.dm.dto.response.ReadDmMessageResponse;
import innerchat.domain.dm.entity.DmMessage;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface DmMessageRepositoryCustom {

    List<ReadDmMessageResponse> findMessagesByCursor(Long dmRoomId, Long cursor, int limit);

    List<DmMessage> findByDmRoomIdOrderByCreatedAtAsc(Long dmRoomId);

    Optional<DmMessage> findTopByDmRoomIdOrderByCreatedAtDesc(Long dmRoomId);

    long countByDmRoomIdAndCreatedAtAfter(Long dmRoomId, LocalDateTime createdAt);
}

