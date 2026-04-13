package innerchat.domain.notification.service;

import java.time.LocalDateTime;

public record NotificationEvent(
        String type,
        String message,
        Long refId,
        LocalDateTime createdAt
) {
    public static NotificationEvent mention(Long messageId, String fromLoginId) {
        return new NotificationEvent("MENTION", "@" + fromLoginId + " mentioned you", messageId, LocalDateTime.now());
    }

    public static NotificationEvent threadReply(Long messageId, String fromLoginId) {
        return new NotificationEvent("THREAD_REPLY", "@" + fromLoginId + " replied in your thread", messageId, LocalDateTime.now());
    }
}
