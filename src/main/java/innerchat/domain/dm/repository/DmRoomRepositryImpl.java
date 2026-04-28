package innerchat.domain.dm.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import innerchat.domain.dm.entity.DmRoomType;
import innerchat.domain.dm.entity.QDmRoom;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@RequiredArgsConstructor
public class DmRoomRepositryImpl implements DmRoomRepositryCustom {

    private static final QDmRoom dmRoom = QDmRoom.dmRoom;

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<Long> findDmRoomIdByTypeAndPairKey(DmRoomType roomType, String pairKey) {
        Long roomId = queryFactory
                .select(dmRoom.dmRoomId)
                .from(dmRoom)
                .where(
                        dmRoom.dmRoomType.eq(roomType),
                        dmRoom.dmPairKey.eq(pairKey)
                )
                .fetchOne();

        return Optional.ofNullable(roomId);
    }
}
