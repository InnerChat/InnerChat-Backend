package innerchat.domain.userStatus.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
public class UserStatusService {

    private final RedisTemplate<String, String> redisTemplate;
    private final SimpMessagingTemplate messagingTemplate;

    public UserStatusService(RedisTemplate<String, String> redisTemplate,
                             @Lazy SimpMessagingTemplate messagingTemplate) {
        this.redisTemplate = redisTemplate;
        this.messagingTemplate = messagingTemplate;
    }

    private static final String STATUS_KEY_PREFIX = "user:status:";

    // ONLINE 상태로 변경 + 브로드캐스트
    public void setOnline(Long userId) {
        redisTemplate.opsForValue().set(STATUS_KEY_PREFIX + userId, "ONLINE");
        broadcastStatus(userId, "ONLINE");
        log.info("userId= {} ONLINE", userId);
    }

    //OFFLINE 상태로 변경 + 브로드캐스트
    public void setOffline(Long userId) {
        redisTemplate.opsForValue().set(STATUS_KEY_PREFIX + userId, "OFFLINE");
        broadcastStatus(userId, "OFFLINE");
        log.info("userID= {} OFFLINE", userId);
    }

    //현재 상태 조회
    public String getStatus(Long userId) {
        String status = redisTemplate.opsForValue().get(STATUS_KEY_PREFIX + userId);
        return status != null ? status : "OFFLINE";
    }

    // ONLINE 상태 여부
    public boolean isOnline(Long userId) {
        return "ONLINE".equals(getStatus(userId));
    }

    //기존 연결에 강제 로그아웃 연결 메시지 전송
    public void forceLogout(Long userId){
        messagingTemplate.convertAndSendToUser(
                userId.toString(),
                "/queue/force-logout",
                Map.of("reason", "다른 기기에서 로그인 되었습니다.")
        );

    }

    //구독자에게 상태 변경 알림
    private void broadcastStatus(Long userId, String status) {
        messagingTemplate.convertAndSend(
                "/topic/status",
                (Object) Map.of("userId", userId, "status", status)
        );

    }
}
