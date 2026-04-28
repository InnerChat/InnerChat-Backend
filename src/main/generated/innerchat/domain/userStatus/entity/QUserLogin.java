package innerchat.domain.userStatus.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QUserLogin is a Querydsl query type for UserLogin
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUserLogin extends EntityPathBase<UserLogin> {

    private static final long serialVersionUID = 1902885178L;

    public static final QUserLogin userLogin = new QUserLogin("userLogin");

    public final EnumPath<UserCurrentStatus> currentStatus = createEnum("currentStatus", UserCurrentStatus.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final DateTimePath<java.time.LocalDateTime> loginAt = createDateTime("loginAt", java.time.LocalDateTime.class);

    public final StringPath loginId = createString("loginId");

    public final DateTimePath<java.time.LocalDateTime> logoutAt = createDateTime("logoutAt", java.time.LocalDateTime.class);

    public QUserLogin(String variable) {
        super(UserLogin.class, forVariable(variable));
    }

    public QUserLogin(Path<? extends UserLogin> path) {
        super(path.getType(), path.getMetadata());
    }

    public QUserLogin(PathMetadata metadata) {
        super(UserLogin.class, metadata);
    }

}

