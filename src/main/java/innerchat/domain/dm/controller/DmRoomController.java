package innerchat.domain.dm.controller;

import innerchat.config.auth.AuthPrincipal;
import innerchat.domain.dm.dto.request.CreateDmRoomParticipantsRequest;
import innerchat.domain.dm.dto.request.CreateDmRoomRequest;
import innerchat.domain.dm.dto.request.DeleteDmRoomParticipantsRequest;
import innerchat.domain.dm.dto.request.UpdateLastReadDmMessageRequest;
import innerchat.domain.dm.dto.response.CreateDmRoomResponse;
import innerchat.domain.dm.dto.response.ReadDmRoomListResponse;
import innerchat.domain.dm.service.DmRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/dm")
public class DmRoomController {

    private final DmRoomService dmRoomService;

    @GetMapping
    public ResponseEntity<List<ReadDmRoomListResponse>> getDmRoomList(@AuthPrincipal Long userId) {
        return ResponseEntity.ok(dmRoomService.getDmRoomList(userId));
    }

    @PostMapping
    public ResponseEntity<CreateDmRoomResponse> saveDmRoom(@RequestBody CreateDmRoomRequest req) {
        return ResponseEntity.ok(dmRoomService.saveDmRoom(req));
    }

    @PutMapping("/read")
    public ResponseEntity<Void> setLastReadDmMessage(@AuthPrincipal Long userId, @RequestBody UpdateLastReadDmMessageRequest req) {
        dmRoomService.setLastReadDmMessage(userId, req);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/participants")
    public ResponseEntity<Void> saveDmRoomParticipants(@RequestBody CreateDmRoomParticipantsRequest req) {
        dmRoomService.saveDmRoomParticipants(req);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/participants")
    public ResponseEntity<Void> removeDmRoomParticipants(@AuthPrincipal Long userId, @RequestBody DeleteDmRoomParticipantsRequest req) {
        dmRoomService.removeDmRoomParticipants(userId, req);
        return ResponseEntity.ok().build();
    }
}