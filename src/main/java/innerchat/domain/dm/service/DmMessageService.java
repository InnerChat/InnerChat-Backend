package innerchat.domain.dm.service;

import innerchat.domain.common.entity.MessageStatus;
import innerchat.domain.dm.dto.request.CreateDmMessageSocketRequest;
import innerchat.domain.dm.dto.request.DeleteDmMessageRequest;
import innerchat.domain.dm.dto.request.UpdateDmMessageRequest;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class DmMessageService {

    private static final int DEFAULT_PAGE_SIZE = 30;

    private final DmMessageRepository dmMessageRepository;
    private final DmParticipantRepository dmParticipantRepository;
    private final DmRoomRepositry dmRoomRepositry;
    private final UserRepository userRepository;
    private final DmMessageRedisPublisher dmMessageRedisPublisher;

    @Transactional(readOnly = true)
    public ReadDmMessageCursorResponse readDmMessages(Long dmRoomId, Long cursor) {
        List<ReadDmMessageResponse> fetched = dmMessageRepository.findMessagesByCursor(
                dmRoomId,
                cursor,
                DEFAULT_PAGE_SIZE + 1
        );

        List<ReadDmMessageResponse> messages;
        boolean hasNext = fetched.size() > DEFAULT_PAGE_SIZE;
        if (hasNext) {
            messages = new ArrayList<>(fetched.subList(1, DEFAULT_PAGE_SIZE + 1));
        } else {
            messages = new ArrayList<>(fetched);
        }

        Long nextCursor = hasNext && !messages.isEmpty() ? messages.getFirst().getDmMessageId() : -1;
        return new ReadDmMessageCursorResponse(messages, nextCursor, hasNext);
    }

    public void sendDmMessage(Long userId, CreateDmMessageSocketRequest req) {
        MessagePersistResult persistResult = createAndPersistMessage(
                req.getDmRoomId(),
                userId,
                req.getThreadRootMessageId(),
                req.getContent()
        );

        String authorName = userRepository.findById(userId)
                .map(User::getUserName)
                .orElseThrow(() -> new IllegalStateException("Author not found. userId=" + userId));

        DmMessageCreatedEvent event = new DmMessageCreatedEvent(
                "MESSAGE_CREATED",
                req.getDmRoomId(),
                persistResult.message().getDmMessageId(),
                persistResult.message().getAuthorId(),
                authorName,
                persistResult.message().getThreadRootMessageId(),
                persistResult.message().getContent(),
                persistResult.message().getStatus(),
                persistResult.message().getCreatedAt(),
                persistResult.reopenedUserIdList()
        );

        publishAfterCommit(event);
    }

    public void updateDmMessage(Long userId, UpdateDmMessageRequest req) {
        DmMessage message = dmMessageRepository.findById(req.getDmMessageId())
                .orElseThrow(() -> new RuntimeException("메시지를 찾을 수 없습니다."));
        validateAuthorIfPresent(userId, message);

        message.setContent(req.getContent());
        message.setStatus(MessageStatus.MODIFIED);

        String authorName = userRepository.findById(message.getAuthorId())
                .map(User::getUserName)
                .orElse("Unknown");

        DmMessageCreatedEvent event = new DmMessageCreatedEvent(
                "MESSAGE_UPDATED",
                message.getDmRoomId(),
                message.getDmMessageId(),
                message.getAuthorId(),
                authorName,
                message.getThreadRootMessageId(),
                message.getContent(),
                message.getStatus(),
                LocalDateTime.now(),
                List.of()
        );
        publishAfterCommit(event);
    }

    public void deleteDmMessage(Long userId, DeleteDmMessageRequest req) {
        DmMessage message = dmMessageRepository.findById(req.getDmMessageId())
                .orElseThrow(() -> new RuntimeException("메시지를 찾을 수 없습니다."));
        validateAuthorIfPresent(userId, message);

        message.setStatus(MessageStatus.DELETED);

        String authorName = userRepository.findById(message.getAuthorId())
                .map(User::getUserName)
                .orElse("Unknown");

        DmMessageCreatedEvent event = new DmMessageCreatedEvent(
                "MESSAGE_DELETED",
                message.getDmRoomId(),
                message.getDmMessageId(),
                message.getAuthorId(),
                authorName,
                message.getThreadRootMessageId(),
                message.getContent(),
                message.getStatus(),
                LocalDateTime.now(),
                List.of()
        );
        publishAfterCommit(event);
    }

    private void validateAuthorIfPresent(Long userId, DmMessage message) {
        if (userId == null) {
            return;
        }

        if (!userId.equals(message.getAuthorId())) {
            throw new IllegalArgumentException("작성자만 메시지를 수정/삭제할 수 있습니다.");
        }
    }

    private void publishAfterCommit(DmMessageCreatedEvent event) {
        if (!TransactionSynchronizationManager.isActualTransactionActive()) {
            dmMessageRedisPublisher.publish(event);
            return;
        }

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                dmMessageRedisPublisher.publish(event);
            }
        });
    }

    private MessagePersistResult createAndPersistMessage(
            Long dmRoomId,
            Long userId,
            Long threadRootMessageId,
            String content
    ) {
        validateMessagePayload(dmRoomId, userId, content);

        DmRoom dmRoom = dmRoomRepositry.findById(dmRoomId)
                .orElseThrow(() -> new IllegalArgumentException("DM room not found. dmRoomId=" + dmRoomId));

        List<Long> reopenedUserIdList = reopenInactiveParticipants(dmRoomId);
        DmMessage message = buildDmMessage(dmRoom, userId, content, threadRootMessageId);
        DmMessage saved = dmMessageRepository.save(message);

        dmRoom.setLastMessageId(saved.getDmMessageId());

        DmParticipant sender = dmParticipantRepository.findByUserIdAndDmRoomId(userId, dmRoomId);
        if (sender != null) {
            sender.setLastReadMessageId(saved.getDmMessageId());
        }

        return new MessagePersistResult(saved, reopenedUserIdList);
    }

    private List<Long> reopenInactiveParticipants(Long dmRoomId) {
        List<Long> reopenedUserIds = new ArrayList<>();
        List<DmParticipant> participants = dmParticipantRepository.findAllByDmRoomId(dmRoomId);

        for (DmParticipant participant : participants) {
            if (Boolean.TRUE.equals(participant.getStatus())) {
                continue;
            }

            participant.setStatus(true);
            participant.setJoinendAt(LocalDateTime.now());
            reopenedUserIds.add(participant.getUserId());
        }
        return reopenedUserIds;
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
            throw new IllegalArgumentException("dmRoomId/userId is required");
        }

        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("Message content must not be blank");
        }

        boolean isParticipant = dmParticipantRepository.existsByDmRoomIdAndUserIdAndStatusTrue(dmRoomId, userId);
        if (!isParticipant) {
            throw new IllegalArgumentException("User is not an active participant of this DM room");
        }
    }

    private record MessagePersistResult(DmMessage message, List<Long> reopenedUserIdList) {
    }
}
