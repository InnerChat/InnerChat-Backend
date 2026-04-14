package innerchat.domain.dm.controller;

import innerchat.domain.dm.dto.response.ReadDmRoomListResponse;
import innerchat.domain.dm.service.DmRoomService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/dm")
public class DmRoomController {

    private final DmRoomService dmRoomService;

    @GetMapping
    public ResponseEntity<List<ReadDmRoomListResponse>> getDmRoomList(@RequestParam Long userId) {
        return ResponseEntity.ok(dmRoomService.getDmRoomList(userId));
    }
}
