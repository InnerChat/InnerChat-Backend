package innerchat.domain.dm.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QDmFileAttachment is a Querydsl query type for DmFileAttachment
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QDmFileAttachment extends EntityPathBase<DmFileAttachment> {

    private static final long serialVersionUID = 1223521592L;

    public static final QDmFileAttachment dmFileAttachment = new QDmFileAttachment("dmFileAttachment");

    public final NumberPath<Long> dmFileAttachmentId = createNumber("dmFileAttachmentId", Long.class);

    public final NumberPath<Long> dmMessageId = createNumber("dmMessageId", Long.class);

    public final StringPath mimeType = createString("mimeType");

    public final StringPath originalName = createString("originalName");

    public final NumberPath<Long> sizeBytes = createNumber("sizeBytes", Long.class);

    public final StringPath storedPath = createString("storedPath");

    public QDmFileAttachment(String variable) {
        super(DmFileAttachment.class, forVariable(variable));
    }

    public QDmFileAttachment(Path<? extends DmFileAttachment> path) {
        super(path.getType(), path.getMetadata());
    }

    public QDmFileAttachment(PathMetadata metadata) {
        super(DmFileAttachment.class, metadata);
    }

}

