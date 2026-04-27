package innerchat.domain.channel.controller;

import innerchat.domain.channel.dto.SendChannelMessageRequest;
import innerchat.domain.channel.service.ChannelMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class ChannelStompController {

    private final ChannelMessageService channelMessageService;

    @MessageMapping("/channel/send")
    public void sendMessage(@Payload SendChannelMessageRequest request, Principal principal) {
        Long authorId = Long.parseLong(principal.getName());
        channelMessageService.sendChannelMessage(
                authorId,
                request.getChannelId(),
                request.getContent(),
                request.getThreadRootMessageId()
        );
    }
}
