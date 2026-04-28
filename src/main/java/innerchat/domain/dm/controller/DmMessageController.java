package innerchat.domain.dm.controller;

import innerchat.config.auth.AuthPrincipal;
import innerchat.domain.dm.dto.request.DeleteDmMessageRequest;
import innerchat.domain.dm.dto.request.ReadDmMessageCursorRequest;
import innerchat.domain.dm.dto.request.UpdateDmMessageRequest;
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

    @GetMapping("/{dmRoomId}")
    public ResponseEntity<ReadDmMessageCursorResponse> readDmMessages(
            @PathVariable Long dmRoomId,
            @ModelAttribute ReadDmMessageCursorRequest req
    ) {
        return ResponseEntity.ok(dmMessageService.readDmMessages(dmRoomId, req.getCursor()));
    }

    @PutMapping
    public ResponseEntity<Void> updateDmMessage(@AuthPrincipal Long userId, @RequestBody UpdateDmMessageRequest req) {
        dmMessageService.updateDmMessage(userId, req);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteDmMessage(@AuthPrincipal Long userId, @RequestBody DeleteDmMessageRequest req) {
        dmMessageService.deleteDmMessage(userId, req);
        return ResponseEntity.ok().build();
    }

}
