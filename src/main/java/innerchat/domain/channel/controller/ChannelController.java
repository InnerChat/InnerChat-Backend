package innerchat.domain.channel.controller;

import innerchat.config.auth.AuthPrincipal;
import innerchat.domain.channel.dto.ChannelMemberResponse;
import innerchat.domain.channel.dto.CreateChannelRequest;
import innerchat.domain.channel.dto.CreateChannelResponse;
import innerchat.domain.channel.dto.InviteChannelRequest;
import innerchat.domain.channel.dto.ReadChannelListResponse;
import innerchat.domain.channel.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/channel")
public class ChannelController {

    private final ChannelService channelService;

    @GetMapping
    public ResponseEntity<List<ReadChannelListResponse>> getChannelList(@AuthPrincipal Long userId) {
        return ResponseEntity.ok(channelService.getChannelList(userId));
    }

    @PostMapping
    public ResponseEntity<CreateChannelResponse> createChannel(
            @RequestBody CreateChannelRequest createChannelRequest,
            @AuthPrincipal Long userId
    ) {
        return ResponseEntity.ok(channelService.createChannel(userId, createChannelRequest));
    }

    @DeleteMapping("/{channelId}")
    public ResponseEntity<Void> deleteChannel(
            @AuthPrincipal Long userId,
            @PathVariable Long channelId
    ) {
        channelService.deleteChannel(userId, channelId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{channelId}/join")
    public ResponseEntity<Void> joinChannel(@AuthPrincipal Long userId, @PathVariable Long channelId) {
        channelService.joinChannel(userId, channelId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{channelId}/members")
    public ResponseEntity<List<ChannelMemberResponse>> getChannelMembers(@PathVariable Long channelId) {
        return ResponseEntity.ok(channelService.getChannelMembers(channelId));
    }

    @PostMapping("/{channelId}/invite")
    public ResponseEntity<Void> inviteToChannel(@AuthPrincipal Long userId, @PathVariable Long channelId, @RequestBody InviteChannelRequest request) {
        channelService.inviteChannel(userId, channelId, request.getTargetUserId());
        return ResponseEntity.ok().build();
    }
}
