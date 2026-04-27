package innerchat.domain.channel.service;

import innerchat.domain.channel.dto.ChannelMessageCreatedEvent;
import innerchat.domain.channel.dto.ChannelMessageResponse;
import innerchat.domain.channel.entity.ChannelMessage;
import innerchat.domain.channel.repository.ChannelMemberRepository;
import innerchat.domain.channel.repository.ChannelMessageRepository;
import innerchat.domain.channel.repository.ChannelRepository;
import innerchat.domain.user.entity.User;
import innerchat.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ChannelMessageService {

    private static final int PAGE_SIZE = 30;

    private final ChannelRepository channelRepository;
    private final ChannelMemberRepository channelMemberRepository;
    private final ChannelMessageRepository channelMessageRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    // 메시지 목록 조회 (커서 기반 페이징)
    public List<ChannelMessageResponse> readChannelMessages(Long userId, Long channelId, Long cursor) {
        if (!channelRepository.existsById(channelId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "채널이 존재하지 않습니다.");
        }
        if (!channelMemberRepository.existsByChannelIdAndUserId(channelId, userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "채널 멤버만 메시지를 조회할 수 있습니다.");
        }

        List<ChannelMessage> messages = channelMessageRepository
                .findMessagesByCursor(channelId, cursor, PageRequest.of(0, PAGE_SIZE));

        // authorId → userName 일괄 조회
        List<Long> authorIds = messages.stream().map(ChannelMessage::getAuthorId).distinct().toList();
        Map<Long, String> nameByUserId = userRepository.findAllById(authorIds).stream()
                .collect(Collectors.toMap(User::getUserId, User::getUserName));

        return messages.stream()
                .map(msg -> new ChannelMessageResponse(msg, nameByUserId.getOrDefault(msg.getAuthorId(), "Unknown")))
                .toList();
    }

    // STOMP 메시지 전송 처리
    public void sendChannelMessage(Long authorId, Long channelId, String content, Long threadRootMessageId) {
        if (!channelRepository.existsById(channelId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "채널이 존재하지 않습니다.");
        }
        if (!channelMemberRepository.existsByChannelIdAndUserId(channelId, authorId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "채널 멤버만 메시지를 보낼 수 있습니다.");
        }

        ChannelMessage saved = channelMessageRepository.save(
                ChannelMessage.of(channelId, authorId, content, threadRootMessageId)
        );

        String authorName = userRepository.findById(authorId)
                .map(User::getUserName)
                .orElse("Unknown");

        ChannelMessageCreatedEvent event = new ChannelMessageCreatedEvent(
                saved.getChannelMessageId(),
                channelId,
                authorId,
                authorName,
                saved.getContent(),
                saved.getStatus().name(),
                saved.getCreatedAt()
        );

        // 채널 구독자 전체에게 브로드캐스트
        messagingTemplate.convertAndSend("/topic/channel/" + channelId, event);
    }

}
