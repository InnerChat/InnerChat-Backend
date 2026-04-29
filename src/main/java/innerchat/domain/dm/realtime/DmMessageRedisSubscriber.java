package innerchat.domain.dm.realtime;

import com.fasterxml.jackson.databind.ObjectMapper;
import innerchat.domain.dm.dto.response.DmInboxEvent;
import innerchat.domain.dm.dto.response.DmMessageCreatedEvent;
import innerchat.domain.dm.entity.DmParticipant;
import innerchat.domain.dm.repository.DmParticipantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@RequiredArgsConstructor
public class DmMessageRedisSubscriber implements MessageListener {

    private final ObjectMapper objectMapper;
    private final SimpMessagingTemplate messagingTemplate;
    private final DmParticipantRepository dmParticipantRepository;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String payload = new String(message.getBody(), StandardCharsets.UTF_8);
        try {
            DmMessageCreatedEvent event = objectMapper.readValue(payload, DmMessageCreatedEvent.class);
            messagingTemplate.convertAndSend("/topic/dm/rooms/" + event.getDmRoomId(), event);

            if (!"MESSAGE_CREATED".equals(event.getEventType())) {
                return;
            }

            DmInboxEvent inboxEvent = new DmInboxEvent(
                    "DM_MESSAGE_CREATED",
                    event.getDmRoomId(),
                    event.getDmMessageId(),
                    event.getAuthorId()
            );

            for (DmParticipant participant : dmParticipantRepository.findAllByDmRoomId(event.getDmRoomId())) {
                if (participant.getUserId().equals(event.getAuthorId())) {
                    continue;
                }

                messagingTemplate.convertAndSendToUser(
                        participant.getUserId().toString(),
                        "/queue/dm/events",
                        inboxEvent
                );
            }
        } catch (Exception e) {
            log.error("Failed to handle DM message event from Redis", e);
        }
    }

}
