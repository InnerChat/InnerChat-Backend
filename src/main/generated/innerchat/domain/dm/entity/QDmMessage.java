package innerchat.domain.dm.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QDmMessage is a Querydsl query type for DmMessage
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QDmMessage extends EntityPathBase<DmMessage> {

    private static final long serialVersionUID = -818327378L;

    public static final QDmMessage dmMessage = new QDmMessage("dmMessage");

    public final NumberPath<Long> authorId = createNumber("authorId", Long.class);

    public final StringPath content = createString("content");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> dmMessageId = createNumber("dmMessageId", Long.class);

    public final NumberPath<Long> dmRoomId = createNumber("dmRoomId", Long.class);

    public final EnumPath<innerchat.domain.common.entity.MessageStatus> status = createEnum("status", innerchat.domain.common.entity.MessageStatus.class);

    public final NumberPath<Long> threadRootMessageId = createNumber("threadRootMessageId", Long.class);

    public QDmMessage(String variable) {
        super(DmMessage.class, forVariable(variable));
    }

    public QDmMessage(Path<? extends DmMessage> path) {
        super(path.getType(), path.getMetadata());
    }

    public QDmMessage(PathMetadata metadata) {
        super(DmMessage.class, metadata);
    }

}

