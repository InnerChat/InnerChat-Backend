package innerchat.domain.dm.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QDmRoom is a Querydsl query type for DmRoom
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QDmRoom extends EntityPathBase<DmRoom> {

    private static final long serialVersionUID = 522602900L;

    public static final QDmRoom dmRoom = new QDmRoom("dmRoom");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final StringPath dmPairKey = createString("dmPairKey");

    public final NumberPath<Long> dmRoomId = createNumber("dmRoomId", Long.class);

    public final EnumPath<DmRoomType> dmRoomType = createEnum("dmRoomType", DmRoomType.class);

    public final NumberPath<Long> lastMessageId = createNumber("lastMessageId", Long.class);

    public final NumberPath<Long> workspaceId = createNumber("workspaceId", Long.class);

    public QDmRoom(String variable) {
        super(DmRoom.class, forVariable(variable));
    }

    public QDmRoom(Path<? extends DmRoom> path) {
        super(path.getType(), path.getMetadata());
    }

    public QDmRoom(PathMetadata metadata) {
        super(DmRoom.class, metadata);
    }

}

