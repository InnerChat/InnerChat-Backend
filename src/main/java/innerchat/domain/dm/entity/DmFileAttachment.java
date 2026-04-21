package innerchat.domain.dm.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "dm_file_attachment")
public class DmFileAttachment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long dmFileAttachmentId;

    @Column(nullable = false)
    private Long dmMessageId;

    @Column(nullable = false)
    private String originalName;

    @Column(nullable = false, length = 500)
    private String storedPath;

    @Column(nullable = false, length = 150)
    private String mimeType;

    @Column(nullable = false)
    private Long sizeBytes;

    public DmFileAttachment(Long dmMessageId,
                            String originalName,
                            String storedPath,
                            String mimeType,
                            Long sizeBytes) {
        this.dmMessageId = dmMessageId;
        this.originalName = originalName;
        this.storedPath = storedPath;
        this.mimeType = mimeType;
        this.sizeBytes = sizeBytes;
    }

}
