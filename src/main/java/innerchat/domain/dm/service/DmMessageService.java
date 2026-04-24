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
            messages = new ArrayList<>(fetched.subList(1, DEFAULT_PAGE_SIZE + 1));
        } else {
            messages = new ArrayList<>(fetched);
        }

        Long nextCursor = hasNext && !messages.isEmpty() ? messages.getFirst().getDmMessageId() : -1;
        return new ReadDmMessageCursorResponse(messages, nextCursor, hasNext);
    }

    @Transactional
    public void sendDmMessage(Long userId, CreateDmMessageSocketRequest req) {
        log.info("DM send requested: senderUserId={}, dmRoomId={}, contentLength={}",
                userId, req.getDmRoomId(), req.getContent() == null ? 0 : req.getContent().length());
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

        log.info("DM message persisted: dmRoomId={}, dmMessageId={}, reopenedUserIds={}",
                event.getDmRoomId(), event.getDmMessageId(), event.getReopenedUserIdList());
        publishAfterCommit(event);
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

        if (!reopenedUserIds.isEmpty()) {
            log.info("DM participants reopened: dmRoomId={}, reopenedUserIds={}", dmRoomId, reopenedUserIds);
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
