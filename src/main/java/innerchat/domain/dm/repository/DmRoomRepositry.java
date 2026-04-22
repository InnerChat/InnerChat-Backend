package innerchat.domain.dm.repository;

import innerchat.domain.dm.dto.DmRoomListNativeProjection;
import innerchat.domain.dm.entity.DmRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DmRoomRepositry extends JpaRepository<DmRoom, Long>, DmRoomRepositryCustom {

    @Query(value = """
            select
                dr.dm_room_id as dmRoomId,
                coalesce((
                    select string_agg(u.user_name, ',')
                    from dm_participants dp2
                    join users u on u.user_id = dp2.user_id
                    where dp2.dm_room_id = dr.dm_room_id
                      and dp2.user_id <> :userId
                      and dp2.status = true
                ), '') as participantNameListRaw,
                dr.last_message_id as lastMessageId,
                (dr.last_message_id - dp.last_read_message_id) as unreadCount
            from dm_participants dp
            join dm_rooms dr on dp.dm_room_id = dr.dm_room_id
            where dp.user_id = :userId
              and dp.status = true
            """, nativeQuery = true)
    List<DmRoomListNativeProjection> getDmRoomListNative(@Param("userId") Long userId);

}
