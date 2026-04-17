package innerchat.domain.dm.service;

import innerchat.domain.dm.dto.request.CreateDmMessageRequest;
import innerchat.domain.dm.dto.response.ReadDmMessageCursorResponse;
import innerchat.domain.dm.dto.response.ReadDmMessageResponse;
import innerchat.domain.dm.entity.DmMessage;
import innerchat.domain.dm.repository.DmMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DmMessageService {

    private static final int DEFAULT_PAGE_SIZE = 30;

    private final DmMessageRepository dmMessageRepository;

    public ReadDmMessageCursorResponse readDmMessages(Long dmRoomId, Long cursor) {
        List<ReadDmMessageResponse> fetched = dmMessageRepository.findMessagesByCursor(
                dmRoomId,
                cursor,
                DEFAULT_PAGE_SIZE + 1
        );

        List<ReadDmMessageResponse> messages = List.of();
        boolean hasNext = fetched.size() > DEFAULT_PAGE_SIZE;
        if (hasNext) {
            messages = new ArrayList<>(fetched.subList(0, DEFAULT_PAGE_SIZE));
        }

        Long nextCursor = hasNext && !messages.isEmpty() ? messages.getLast().getDmMessageId() : -1;
        return new ReadDmMessageCursorResponse(messages, nextCursor, hasNext);
    }

    @Transactional
    public void createDmMessage(CreateDmMessageRequest req) {

        DmMessage entity;
        if (req.getThreadRootMessageId() != -1) {
            entity = new DmMessage(req.getWorkspaceId(),
                    req.getDmRoomId(),
                    req.getUserId(),
                    req.getContent());
        } else {
            entity = new DmMessage(req.getWorkspaceId(),
                    req.getDmRoomId(),
                    req.getUserId(),
                    req.getContent(),
                    req.getThreadRootMessageId());
        }


    }

}
