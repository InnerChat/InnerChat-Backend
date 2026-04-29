package innerchat.domain.dm.realtime;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

@Configuration
@RequiredArgsConstructor
public class DmRedisPubSubConfig {

    private static final String DM_MESSAGE_CREATED_TOPIC = "dm:message:created";

    private final DmMessageRedisSubscriber dmMessageRedisSubscriber;

    @Bean
    public ChannelTopic dmMessageCreatedTopic() {
        return new ChannelTopic(DM_MESSAGE_CREATED_TOPIC);
    }

    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(
            RedisConnectionFactory redisConnectionFactory,
            ChannelTopic dmMessageCreatedTopic
    ) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(redisConnectionFactory);
        container.addMessageListener(dmMessageRedisSubscriber, dmMessageCreatedTopic);
        return container;
    }
}
