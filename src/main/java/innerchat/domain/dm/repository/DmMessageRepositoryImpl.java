package innerchat.domain.dm.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import innerchat.domain.common.entity.MessageStatus;
import innerchat.domain.dm.dto.response.ReadDmMessageResponse;
import innerchat.domain.dm.entity.DmMessage;
import innerchat.domain.dm.entity.QDmMessage;
import innerchat.domain.user.entity.QUser;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class DmMessageRepositoryImpl implements DmMessageRepositoryCustom {

    private static final QDmMessage dmMessage = QDmMessage.dmMessage;
    private static final QUser user = QUser.user;

    private final JPAQueryFactory queryFactory;

    @Override
    public List<ReadDmMessageResponse> findMessagesByCursor(Long dmRoomId, Long cursor, int limit) {
        List<Long> pagedMessageIds = queryFactory
                .select(dmMessage.dmMessageId)
                .from(dmMessage)
                .where(
                        dmMessage.dmRoomId.eq(dmRoomId),
                        dmMessage.status.ne(MessageStatus.DELETED),
                        dmMessageIdLt(cursor)
                )
                .orderBy(dmMessage.dmMessageId.desc())
                .limit(limit)
                .fetch();

        if (pagedMessageIds.isEmpty()) {
            return List.of();
        }

        return queryFactory
                .select(Projections.constructor(ReadDmMessageResponse.class,
                        dmMessage.dmMessageId,
                        dmMessage.authorId,
                        user.userName,
                        dmMessage.content,
                        dmMessage.status,
                        dmMessage.createdAt))
                .from(dmMessage)
                .join(user).on(dmMessage.authorId.eq(user.userId))
                .where(
                        dmMessage.dmRoomId.eq(dmRoomId),
                        dmMessage.dmMessageId.in(pagedMessageIds)
                )
                .orderBy(dmMessage.dmMessageId.asc())
                .fetch();
    }

    @Override
    public List<DmMessage> findByDmRoomIdOrderByCreatedAtAsc(Long dmRoomId) {
        return queryFactory
                .selectFrom(dmMessage)
                .where(dmMessage.dmRoomId.eq(dmRoomId))
                .orderBy(dmMessage.createdAt.asc())
                .fetch();
    }

    @Override
    public Optional<DmMessage> findTopByDmRoomIdOrderByCreatedAtDesc(Long dmRoomId) {
        DmMessage result = queryFactory
                .selectFrom(dmMessage)
                .where(dmMessage.dmRoomId.eq(dmRoomId))
                .orderBy(dmMessage.createdAt.desc())
                .limit(1)
                .fetchOne();

        return Optional.ofNullable(result);
    }

    @Override
    public long countByDmRoomIdAndCreatedAtAfter(Long dmRoomId, LocalDateTime createdAt) {
        Long count = queryFactory
                .select(dmMessage.count())
                .from(dmMessage)
                .where(
                        dmMessage.dmRoomId.eq(dmRoomId),
                        dmMessage.createdAt.after(createdAt)
                )
                .fetchOne();

        return count == null ? 0L : count;
    }

    private BooleanExpression dmMessageIdLt(Long cursor) {
        return cursor == null ? null : dmMessage.dmMessageId.lt(cursor);
    }
}
