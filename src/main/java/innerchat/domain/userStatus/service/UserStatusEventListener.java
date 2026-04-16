package innerchat.domain.userStatus.service;

import innerchat.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.security.Principal;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserStatusEventListener {

    private final UserStatusService userStatusService;
    private final UserRepository userRepository;

    /**
     * STOMP 연결 이벤트
     * @param event
     */
    @Async
    @EventListener
    public void handlerConnect(SessionConnectedEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        Principal user = accessor.getUser();
        if (user == null) {
            return;
        }

        Long userId = Long.parseLong(user.getName()); //식별자
        userStatusService.setOnline(userId);

        String loginId = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("존재하지 않는 userId: " + userId))
                .getLoginId();

        log.info("STOMP 연결 완료 userId= {}", userId);
    }

    /**
     * STOMP 연결 해제 이벤트
     * @param event
     */
    @Async
    @EventListener
    public void handlerDisconnect(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        Principal user = accessor.getUser();
        if (user == null) return;

        Long userId = Long.parseLong(user.getName());
        userStatusService.setOffline(userId);

        String loginId = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("존재하지 않는 userId: " + userId))
                .getLoginId();

        log.info("STOMP 연결 해제 userId= {}", userId);

    }

}
