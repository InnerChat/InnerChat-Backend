package innerchat.domain.channel.controller;

import innerchat.config.auth.AuthPrincipal;
import innerchat.domain.channel.dto.ChannelMessageResponse;
import innerchat.domain.channel.service.ChannelMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/channel")
public class ChannelMessageController {
    private final ChannelMessageService channelMessageService;

    // GET /channel/{channelId}/messages?cursor={lastMessageId}
    @GetMapping("/{channelId}/messages")
    public ResponseEntity<List<ChannelMessageResponse>> getMessages(
            @AuthPrincipal Long userId,
            @PathVariable Long channelId,
            @RequestParam(required = false) Long cursor
    ) {
        return ResponseEntity.ok(channelMessageService.readChannelMessages(userId, channelId, cursor));
    }
}
