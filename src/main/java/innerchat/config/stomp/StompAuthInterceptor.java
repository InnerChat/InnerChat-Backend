package innerchat.config.stomp;

import innerchat.config.jwt.JwtProvider;

import innerchat.domain.userStatus.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class StompAuthInterceptor implements ChannelInterceptor {

    private final JwtProvider jwtProvider;
    private final UserStatusService userStatusService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor == null || !StompCommand.CONNECT.equals(accessor.getCommand())) {
            return message;
        }

        //JWT 검증
        String authHeader = accessor.getFirstNativeHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new MessageDeliveryException("Authorization 헤더가 없습니다");
        }

        String token = authHeader.substring(7);
        if (!jwtProvider.validate(token)) {
            throw new MessageDeliveryException("유효하지 않은 토큰 입니다.");
        }

        Long userId = jwtProvider.getUserId(token);

        //중복 연결 처리: 기존 연결 강제 로그아웃
        if (userStatusService.isOnline(userId)) {
            log.info("중복 연결 감지 userId= {}, 기존 연결 강제 종료", userId);
            userStatusService.forceLogout(userId);
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        //STMOP 세션에 userId 저장 (EventListener에서 사용)
        accessor.setUser(new StompPrincipal(userId));
        log.info("STMOP CONNECT 인증 완료 userId={}", userId);

        return message;
    }
}
