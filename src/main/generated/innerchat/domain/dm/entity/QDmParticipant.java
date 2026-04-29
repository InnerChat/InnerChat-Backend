package innerchat.domain.dm.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QDmParticipant is a Querydsl query type for DmParticipant
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QDmParticipant extends EntityPathBase<DmParticipant> {

    private static final long serialVersionUID = 1570083930L;

    public static final QDmParticipant dmParticipant = new QDmParticipant("dmParticipant");

    public final NumberPath<Long> dmParticipantId = createNumber("dmParticipantId", Long.class);

    public final NumberPath<Long> dmRoomId = createNumber("dmRoomId", Long.class);

    public final DateTimePath<java.time.LocalDateTime> joinendAt = createDateTime("joinendAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> lastReadMessageId = createNumber("lastReadMessageId", Long.class);

    public final BooleanPath status = createBoolean("status");

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public QDmParticipant(String variable) {
        super(DmParticipant.class, forVariable(variable));
    }

    public QDmParticipant(Path<? extends DmParticipant> path) {
        super(path.getType(), path.getMetadata());
    }

    public QDmParticipant(PathMetadata metadata) {
        super(DmParticipant.class, metadata);
    }

}

