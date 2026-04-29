package innerchat.domain.channel.service;

import innerchat.domain.channel.dto.*;
import innerchat.domain.channel.dto.ChannelMemberEvent.EventType;
import innerchat.domain.channel.entity.Channel;
import innerchat.domain.channel.entity.ChannelMember;
import innerchat.domain.channel.entity.ChannelMessage;
import innerchat.domain.channel.entity.ChannelMessageStatus;
import innerchat.domain.channel.repository.ChannelMemberRepository;
import innerchat.domain.channel.repository.ChannelMessageRepository;
import innerchat.domain.channel.repository.ChannelRepository;
import innerchat.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ChannelService {

    private final ChannelRepository channelRepository;
    private final ChannelMemberRepository channelMemberRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final ChannelMessageRepository channelMessageRepository;


    /**
     * 트랜잭션 주의
     * @Transactional` 클래스에서 `SimpMessagingTemplate.convertAndSend`는 트랜잭션 커밋 이전에 실행될 수 있다.
     * DB 반영이 보장된 후 이벤트를 발행하려면 `@TransactionalEventListener` 또는 `TransactionSynchronizationManager`를 사용한다.
     * 현재 규모에서는 단순 호출로도 실용상 문제없으나, 향후 개선 포인트로 남긴다.
     * @param channelId
     * @param userId
     * @param eventType
     */
    private void broadcastMemberEvent(Long channelId, Long userId, EventType eventType) {
        String userName = userRepository.findById(userId)
                .map(user -> user.getUserName())
                .orElse("Unknown");
        log.info("broadcastMemberEvent eventType={} channelId={} userId={}", eventType, channelId, userId);

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                try {
                    messagingTemplate.convertAndSend(
                            "/topic/channel/" + channelId + "/members",
                            new ChannelMemberEvent(eventType, channelId, userId, userName)
                    );
                    log.info("convertAndSend 완료 /topic/channel/{}/members", channelId);
                } catch (Exception e) {
                    log.error("convertAndSend 실패", e);
                }

                if (eventType == EventType.MEMBER_INVITED) {
                    try {
//                        /user/2/queue/channel/events
//                        /queue/channel/events-userId세션값
                        messagingTemplate.convertAndSendToUser(
                                userId.toString(),
                                "/queue/channel/events",
                                new ChannelMemberEvent(eventType, channelId, userId, userName)
                        );
                        log.info("convertAndSendToUser 완료 targetUserId={}", userId);
                    } catch (Exception e) {
                        log.error("convertAndSendToUser 실패 targetUserId={}", userId, e);
                    }
                }
            }
        });
    }

    /**
     * 채널 전체 목록 가져오기
     * @return
     */
    public List<ReadChannelListResponse> getChannelList(Long userId) {
        return channelRepository.findAll().stream()
                .map(channel -> new ReadChannelListResponse(
                        channel.getChannelId(),
                        channel.getName(),
                        channel.getDescription(),
                        channel.getType(),
                        channel.getOwnerId(),
                        channelMemberRepository.countByChannelId(channel.getChannelId()),
                        channelMemberRepository.existsByChannelIdAndUserId(channel.getChannelId(), userId)
                ))
                .toList();
    }

    public List<ReadChannelListResponse> getChannelListByUserId(Long userId) {
        List<Long> channelIds = channelMemberRepository.findAllByUserId(userId)
                .stream()
                .map(ChannelMember::getChannelId)
                .toList();
        return channelRepository.findAllById(channelIds)
                .stream()
                .map(channel -> new ReadChannelListResponse(
                        channel.getChannelId(),
                        channel.getName(),
                        channel.getDescription(),
                        channel.getType(),
                        channel.getOwnerId(),
                        channelMemberRepository.countByChannelId(channel.getChannelId()),
                        true
                ))
                .toList();
    }

    /**
     * 채널 생성
     * @param userId
     * @param request
     * @return
     */
    public CreateChannelResponse createChannel(Long userId, CreateChannelRequest request) {
        if (request.getChannelName() == null  || request.getChannelName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "채널 이름이 필요합니다.");
        }
        if (channelRepository.existsByName(request.getChannelName())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 존재하는 채널 이름입니다.");
        }
        Channel saved = channelRepository.save(new Channel(request.getChannelName(), request.getDescription(), userId, request.getType()));

        //생성자는 맴버로 자동 등록
        channelMemberRepository.save(new ChannelMember(saved.getChannelId(), userId));

        return new CreateChannelResponse(saved.getChannelId());
    }

    /**
     * 채널 삭제 프론트 미구현
     * @param userId
     * @param channelId
     */
    public void deleteChannel(Long userId, Long channelId) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "채널이 존재하지 않습니다."));

        if (!channel.getOwnerId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "채널 삭제 권한이 없습니다.");
        }

        channelMemberRepository.deleteAllByChannelId(channelId);
        channelRepository.delete(channel);
    }

    /**
     * 채널 참가 프론트 미구현
     * @param userId
     * @param channelId
     */
    public void joinChannel(Long userId, Long channelId) {
        if (!channelRepository.existsById(channelId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "채널이 존재하지 않습니다.");
        }
        if (channelMemberRepository.existsByChannelIdAndUserId(channelId, userId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 참가한 채널 입니다.");
        }
        channelMemberRepository.save(new ChannelMember(channelId, userId));

        //브로드캐스트 추가
        broadcastMemberEvent(channelId, userId, EventType.MEMBER_JOINED);
    }

    public List<ChannelMemberResponse> getChannelMembers(Long channelId) {
        List<Long> userIds = channelMemberRepository.findAllByChannelId(channelId)
                .stream()
                .map(ChannelMember::getUserId)
                .toList();
        return userRepository.findAllById(userIds)
                .stream()
                .map(user -> new ChannelMemberResponse(user.getUserId(), user.getUserName()))
                .toList();
    }

    public void inviteChannel(Long inviterId, Long channelId, Long targetUserId) {
        if (!channelRepository.existsById(channelId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "채널이 존재하지 않습니다.");
        }
        if (!channelMemberRepository.existsByChannelIdAndUserId(channelId, inviterId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "채널 맴버만 초대할 수 있습니다.");
        }
        if (channelMemberRepository.existsByChannelIdAndUserId(channelId, targetUserId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 채널 맴버 입니다.");
        }
        channelMemberRepository.save(new ChannelMember(channelId, targetUserId));

        // 채널 전체 멤버 변동 이벤트 + 초대 당사자 개인 큐 전송 (afterCommit 보장)
        broadcastMemberEvent(channelId, targetUserId, EventType.MEMBER_INVITED);
    }

    public void deleteChannelMembers(Long userId, Long channelId) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "채널이 존재하지 않습니다."));

        if (channel.getOwnerId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "채널 소유자는 나갈 수 없습니다. 채널을 삭제하거나 소유권을 이전하세요.");
        }

        if (!channelMemberRepository.existsByChannelIdAndUserId(channelId, userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "채널 멤버가 아닙니다.");
        }

        channelMemberRepository.deleteByChannelIdAndUserId(channelId, userId);
        broadcastMemberEvent(channelId, userId, EventType.MEMBER_LEFT);
    }

    public void messageModify(Long userId, MessageModifyRequest request) {
        ChannelMessage found = channelMessageRepository.findById(request.getChannelMessageId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "메시지가 존재하지 않습니다."));

        if (!found.getAuthorId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "메시지 수정/삭제 권한이 없습니다.");
        }

        if (ChannelMessageStatus.MODIFIED.equals(request.getAction())) {
            if (request.getContent() == null || request.getContent().isBlank()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정할 내용이 필요합니다.");
            }
            found.setContent(request.getContent());
            found.setStatus(ChannelMessageStatus.MODIFIED);

        } else if (ChannelMessageStatus.DELETED.equals(request.getAction())) {
            found.setContent("삭제된 메시지입니다.");
            found.setStatus(ChannelMessageStatus.DELETED);

        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "지원하지 않는 action입니다.");
        }

        Long channelId = found.getChannelId();
        ChannelMessageStatus finalStatus = found.getStatus();
        String finalContent = found.getContent();
        Long messageId = found.getChannelMessageId();

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {

                messagingTemplate.convertAndSend(
                        "/topic/channel/" + channelId + "/messages",
                        new ChannelMessageModifiedEvent(messageId, channelId, finalContent, finalStatus)
                );
            }
        });
    }
}
