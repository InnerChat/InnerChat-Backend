package innerchat.domain.dm.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QDmMessageMention is a Querydsl query type for DmMessageMention
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QDmMessageMention extends EntityPathBase<DmMessageMention> {

    private static final long serialVersionUID = -476650148L;

    public static final QDmMessageMention dmMessageMention = new QDmMessageMention("dmMessageMention");

    public final NumberPath<Long> dmMessageId = createNumber("dmMessageId", Long.class);

    public final NumberPath<Long> dmMessageMentionId = createNumber("dmMessageMentionId", Long.class);

    public final NumberPath<Long> mentionChannelId = createNumber("mentionChannelId", Long.class);

    public final EnumPath<innerchat.domain.common.entity.MentionType> mentionType = createEnum("mentionType", innerchat.domain.common.entity.MentionType.class);

    public final NumberPath<Long> mentionUserId = createNumber("mentionUserId", Long.class);

    public QDmMessageMention(String variable) {
        super(DmMessageMention.class, forVariable(variable));
    }

    public QDmMessageMention(Path<? extends DmMessageMention> path) {
        super(path.getType(), path.getMetadata());
    }

    public QDmMessageMention(PathMetadata metadata) {
        super(DmMessageMention.class, metadata);
    }

}

