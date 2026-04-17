package innerchat.domain.dm.controller;

import innerchat.domain.dm.dto.request.CreateDmMessageRequest;
import innerchat.domain.dm.dto.request.ReadDmMessageCursorRequest;
import innerchat.domain.dm.dto.response.ReadDmMessageCursorResponse;
import innerchat.domain.dm.service.DmMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/dm/chat")
public class DmMessageController {

    private final DmMessageService dmMessageService;

    @GetMapping("/{dmRoomId}/messages")
    public ResponseEntity<ReadDmMessageCursorResponse> readDmMessages(
            @PathVariable Long dmRoomId,
            @ModelAttribute ReadDmMessageCursorRequest req
    ) {
        return ResponseEntity.ok(dmMessageService.readDmMessages(dmRoomId, req.getCursor()));
    }

    @PostMapping
    public ResponseEntity<Void> createDmMessage(@RequestBody CreateDmMessageRequest req) {
        dmMessageService.createDmMessage(req);
        return ResponseEntity.ok().build();
    }
}
