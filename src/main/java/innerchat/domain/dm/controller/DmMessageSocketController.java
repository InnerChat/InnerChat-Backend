package innerchat.domain.dm.controller;

import innerchat.domain.dm.dto.request.CreateDmMessageSocketRequest;
import innerchat.domain.dm.dto.request.DeleteDmMessageRequest;
import innerchat.domain.dm.dto.request.UpdateDmMessageRequest;
import innerchat.domain.dm.service.DmMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class DmMessageSocketController {

    private final DmMessageService dmMessageService;

    @MessageMapping("/dm/chat/send")
    public void sendDmMessage(
            Principal principal,
            @Payload CreateDmMessageSocketRequest req
    ) {
        if (principal == null) {
            throw new IllegalArgumentException("소켓 인증 정보가 없습니다.");
        }

        Long userId = Long.parseLong(principal.getName());
        dmMessageService.sendDmMessage(userId, req);
    }

    @MessageMapping("/dm/chat/update")
    public void updateDmMessage(
            Principal principal,
            @Payload UpdateDmMessageRequest req
    ) {
        if (principal == null) {
            throw new IllegalArgumentException("웹소켓 인증 정보가 없습니다.");
        }

        Long userId = Long.parseLong(principal.getName());
        dmMessageService.updateDmMessage(userId, req);
    }

    @MessageMapping("/dm/chat/delete")
    public void deleteDmMessage(
            Principal principal,
            @Payload DeleteDmMessageRequest req
    ) {
        if (principal == null) {
            throw new IllegalArgumentException("웹소켓 인증 정보가 없습니다.");
        }

        Long userId = Long.parseLong(principal.getName());
        dmMessageService.deleteDmMessage(userId, req);
    }
}
