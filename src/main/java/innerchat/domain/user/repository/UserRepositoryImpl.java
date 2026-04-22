package innerchat.domain.user.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import innerchat.domain.user.dto.response.ReadUsersByWorkSpaceResponse;
import innerchat.domain.user.entity.QUser;
import innerchat.domain.user.entity.UserStatus;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepositoryCustom {

    private static final QUser user = QUser.user;
    private final JPAQueryFactory queryFactory;

    @Override
    public List<ReadUsersByWorkSpaceResponse> getUsersByWorkSpace(Long workspaceId) {
        return queryFactory
                .select(Projections.constructor(ReadUsersByWorkSpaceResponse.class,
                        user.userId,
                        user.userName,
                        user.role))
                .from(user)
                .where(user.workspaceId.eq(workspaceId), user.status.eq(UserStatus.ACTIVE))
                .fetch();
    }
}
