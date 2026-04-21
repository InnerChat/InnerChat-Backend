package innerchat.domain.dm.service;

import innerchat.domain.dm.dto.request.CreateDmMessageSocketRequest;
import innerchat.domain.dm.dto.response.DmMessageCreatedEvent;
import innerchat.domain.dm.dto.response.ReadDmMessageCursorResponse;
import innerchat.domain.dm.dto.response.ReadDmMessageResponse;
import innerchat.domain.dm.entity.DmMessage;
import innerchat.domain.dm.entity.DmParticipant;
import innerchat.domain.dm.entity.DmRoom;
import innerchat.domain.dm.realtime.DmMessageRedisPublisher;
import innerchat.domain.dm.repository.DmMessageRepository;
import innerchat.domain.dm.repository.DmParticipantRepository;
import innerchat.domain.dm.repository.DmRoomRepositry;
import innerchat.domain.user.entity.User;
import innerchat.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DmMessageService {

    private static final int DEFAULT_PAGE_SIZE = 30;

    private final DmMessageRepository dmMessageRepository;
    private final DmParticipantRepository dmParticipantRepository;
    private final DmRoomRepositry dmRoomRepositry;
    private final UserRepository userRepository;
    private final DmMessageRedisPublisher dmMessageRedisPublisher;

    public ReadDmMessageCursorResponse readDmMessages(Long dmRoomId, Long cursor) {
        List<ReadDmMessageResponse> fetched = dmMessageRepository.findMessagesByCursor(
                dmRoomId,
                cursor,
                DEFAULT_PAGE_SIZE + 1
        );

        List<ReadDmMessageResponse> messages;
        boolean hasNext = fetched.size() > DEFAULT_PAGE_SIZE;
        if (hasNext) {
            messages = new ArrayList<>(fetched.subList(0, DEFAULT_PAGE_SIZE));
        } else {
            messages = new ArrayList<>(fetched);
        }

        Long nextCursor = hasNext && !messages.isEmpty() ? messages.getLast().getDmMessageId() : -1;
        return new ReadDmMessageCursorResponse(messages, nextCursor, hasNext);
    }

    @Transactional
    public void sendDmMessage(Long userId, CreateDmMessageSocketRequest req) {
        DmMessage saved = createAndPersistMessage(
                req.getDmRoomId(),
                userId,
                req.getThreadRootMessageId(),
                req.getContent()
        );

        String authorName = userRepository.findById(userId)
                .map(User::getUserName)
                .orElseThrow(() -> new IllegalStateException("작성자 정보가 없습니다. userId=" + userId));

        DmMessageCreatedEvent event = new DmMessageCreatedEvent(
                req.getDmRoomId(),
                saved.getDmMessageId(),
                saved.getAuthorId(),
                authorName,
                saved.getThreadRootMessageId(),
                saved.getContent(),
                saved.getStatus(),
                saved.getCreatedAt()
        );

        dmMessageRedisPublisher.publish(event);
    }

    private DmMessage createAndPersistMessage(
            Long dmRoomId,
            Long userId,
            Long threadRootMessageId,
            String content
    ) {
        validateMessagePayload(dmRoomId, userId, content);

        DmRoom dmRoom = dmRoomRepositry.findById(dmRoomId)
                .orElseThrow(() -> new IllegalArgumentException("채팅방이 존재하지 않습니다. dmRoomId=" + dmRoomId));

        DmMessage message = buildDmMessage(dmRoom, userId, content, threadRootMessageId);
        DmMessage saved = dmMessageRepository.save(message);

        dmRoom.setLastMessageId(saved.getDmMessageId());

        DmParticipant sender = dmParticipantRepository.findByUserIdAndDmRoomId(userId, dmRoomId);
        if (sender != null) {
            sender.setLastReadMessageId(saved.getDmMessageId());
        }

        return saved;
    }

    private DmMessage buildDmMessage(
            DmRoom dmRoom,
            Long userId,
            String content,
            Long threadRootMessageId
    ) {
        if (threadRootMessageId == null || threadRootMessageId <= 0L) {
            return new DmMessage(dmRoom.getDmRoomId(), userId, content);
        }

        return new DmMessage(
                dmRoom.getDmRoomId(),
                userId,
                content,
                threadRootMessageId
        );
    }

    private void validateMessagePayload(
            Long dmRoomId,
            Long userId,
            String content
    ) {
        if (dmRoomId == null || userId == null) {
            throw new IllegalArgumentException("dmRoomId/userId는 필수입니다.");
        }

        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("메시지 본문은 비어 있을 수 없습니다.");
        }

        boolean isParticipant = dmParticipantRepository.existsByDmRoomIdAndUserIdAndStatusTrue(dmRoomId, userId);
        if (!isParticipant) {
            throw new IllegalArgumentException("채팅방 참여자가 아닙니다.");
        }
    }
}
