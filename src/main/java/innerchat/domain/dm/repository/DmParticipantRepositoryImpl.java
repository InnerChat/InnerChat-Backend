package innerchat.domain.dm.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import innerchat.domain.dm.entity.DmParticipant;
import innerchat.domain.dm.entity.QDmParticipant;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DmParticipantRepositoryImpl implements DmParticipantRepositoryCustom {

    private static final QDmParticipant dmParticipant = QDmParticipant.dmParticipant;

    private final JPAQueryFactory queryFactory;

    @Override
    public DmParticipant findByUserIdAndDmRoomId(Long userId, Long dmRoomId) {
        return queryFactory
                .selectFrom(dmParticipant)
                .where(
                        dmParticipant.userId.eq(userId),
                        dmParticipant.dmRoomId.eq(dmRoomId)
                )
                .fetchOne();
    }
}

