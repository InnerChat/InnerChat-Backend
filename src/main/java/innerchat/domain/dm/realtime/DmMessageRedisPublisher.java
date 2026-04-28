package innerchat.domain.dm.realtime;

import com.fasterxml.jackson.databind.ObjectMapper;
import innerchat.domain.dm.dto.response.DmMessageCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DmMessageRedisPublisher {

    private final StringRedisTemplate stringRedisTemplate;
    private final ChannelTopic dmMessageCreatedTopic;
    private final ObjectMapper objectMapper;

    public void publish(DmMessageCreatedEvent event) {
        try {
            String payload = objectMapper.writeValueAsString(event);
            stringRedisTemplate.convertAndSend(dmMessageCreatedTopic.getTopic(), payload);
        } catch (Exception e) {
            throw new IllegalStateException("DM message event serialization failed", e);
        }
    }
}
