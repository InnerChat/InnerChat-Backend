package innerchat.domain.channel.repository;

import innerchat.domain.channel.entity.ChannelMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.springframework.data.domain.Pageable;
import java.util.List;

public interface ChannelMessageRepository extends JpaRepository<ChannelMessage, Long> {
    //현재 스크롤 커서 기반
    @Query("SELECT m FROM ChannelMessage m WHERE m.channelId = :channelId " +
            "AND (:cursor IS NULL OR m.channelMessageId < :cursor) " +
            "ORDER BY m.channelMessageId DESC ")
    List<ChannelMessage> findMessagesByCursor(
            @Param("channelId") Long channelId,
            @Param("cursor") Long cursor,
            Pageable pageable);

}
