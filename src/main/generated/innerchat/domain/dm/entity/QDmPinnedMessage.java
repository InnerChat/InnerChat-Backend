package innerchat.domain.dm.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QDmPinnedMessage is a Querydsl query type for DmPinnedMessage
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QDmPinnedMessage extends EntityPathBase<DmPinnedMessage> {

    private static final long serialVersionUID = -1163986826L;

    public static final QDmPinnedMessage dmPinnedMessage = new QDmPinnedMessage("dmPinnedMessage");

    public final NumberPath<Long> dmMessageId = createNumber("dmMessageId", Long.class);

    public final NumberPath<Long> dmPinMessageId = createNumber("dmPinMessageId", Long.class);

    public final NumberPath<Long> dmRoomId = createNumber("dmRoomId", Long.class);

    public final NumberPath<Long> pinnedBy = createNumber("pinnedBy", Long.class);

    public QDmPinnedMessage(String variable) {
        super(DmPinnedMessage.class, forVariable(variable));
    }

    public QDmPinnedMessage(Path<? extends DmPinnedMessage> path) {
        super(path.getType(), path.getMetadata());
    }

    public QDmPinnedMessage(PathMetadata metadata) {
        super(DmPinnedMessage.class, metadata);
    }

}

