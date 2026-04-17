package innerchat.domain.dm.service;

import innerchat.domain.dm.dto.request.CreateDmRoomParticipantsRequest;
import innerchat.domain.dm.dto.request.CreateDmRoomRequest;
import innerchat.domain.dm.dto.request.DeleteDmRoomParticipantsRequest;
import innerchat.domain.dm.dto.request.UpdateLastReadDmMessageRequest;
import innerchat.domain.dm.dto.response.CreateDmRoomResponse;
import innerchat.domain.dm.dto.response.ReadDmRoomListResponse;
import innerchat.domain.dm.entity.DmParticipant;
import innerchat.domain.dm.entity.DmRoom;
import innerchat.domain.dm.entity.DmRoomType;
import innerchat.domain.dm.repository.DmParticipantRepository;
import innerchat.domain.dm.repository.DmRoomRepositry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class DmRoomService {

    private final DmRoomRepositry dmRoomRepositry;
    private final DmParticipantRepository dmParticipantRepository;

    @Transactional(readOnly = true)
    public List<ReadDmRoomListResponse> getDmRoomList(Long userId) {
        return dmRoomRepositry.getDmRoomList(userId);
    }

    public CreateDmRoomResponse saveDmRoom(CreateDmRoomRequest req) {
        List<Long> normalizedParticipantIds = req.getParticipantIdList().stream()
                .distinct()
                .sorted()
                .toList();

        if (normalizedParticipantIds.size() < 2) {
            throw new IllegalArgumentException("참가자 아이디는 적어도 2명 이상이어야 합니다");
        }

        String pairKey = normalizedParticipantIds.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(":"));

        DmRoomType dmRoomType;
        if (normalizedParticipantIds.size() == 2) {
            dmRoomType = DmRoomType.ONE_TO_ONE;
        } else {
            dmRoomType = DmRoomType.GROUP;
        }

        Long existingDmRoomId = dmRoomRepositry.findDmRoomIdByTypeAndPairKey(dmRoomType, pairKey).orElse(null);
        if (existingDmRoomId != null) {
            reOpenParticipants(existingDmRoomId, normalizedParticipantIds);
            return new CreateDmRoomResponse(existingDmRoomId);
        }

        if (req.getWorkspaceId() == null) {
            throw new IllegalArgumentException("워크스페이스 ID 존재하지 않습니다");
        }

        DmRoom savedRoom = dmRoomRepositry.save(
                new DmRoom(req.getWorkspaceId(), dmRoomType, pairKey)
        );

        List<DmParticipant> participants = new ArrayList<>(normalizedParticipantIds.size());
        for (Long participantId : normalizedParticipantIds) {
            participants.add(new DmParticipant(savedRoom.getDmRoomId(), participantId));
        }
        dmParticipantRepository.saveAll(participants);

        return new CreateDmRoomResponse(savedRoom.getDmRoomId());
    }

    private void reOpenParticipants(Long dmRoomId, List<Long> participantIds) {
        List<DmParticipant> toSave = new ArrayList<>();

        for (Long userId : participantIds) {
            DmParticipant participant = dmParticipantRepository.findByUserIdAndDmRoomId(userId, dmRoomId);

            if (participant == null) {
                toSave.add(new DmParticipant(dmRoomId, userId));
                continue;
            }

            if (!participant.getStatus()) {
                participant.setStatus(true);
                participant.setJoinendAt(LocalDateTime.now());
                toSave.add(participant);
            }
        }

        if (!toSave.isEmpty()) {
            dmParticipantRepository.saveAll(toSave);
        }
    }

    public void setLastReadDmMessage(UpdateLastReadDmMessageRequest req) {
        DmRoom dmRoom = dmRoomRepositry.findById(req.getDmRoomId())
                .orElseThrow(() -> new RuntimeException("해당 채팅방이 존재하지 않습니다"));
        DmParticipant dmParticipant = dmParticipantRepository.findByUserIdAndDmRoomId(req.getUserId(), req.getDmRoomId());

        dmParticipant.setLastReadMessageId(dmRoom.getLastMessageId());
    }

    public void saveDmRoomParticipants(CreateDmRoomParticipantsRequest req) {
        List<DmParticipant> list = new ArrayList<>();

        for (Long id : req.getUserIdList()) {
            list.add(new DmParticipant(req.getDmRoomId(), id));
        }

        dmParticipantRepository.saveAll(list);
    }

    public void removeDmRoomParticipants(DeleteDmRoomParticipantsRequest req) {
        DmParticipant participant = dmParticipantRepository.findByUserIdAndDmRoomId(req.getUserId(), req.getDmRoomId());

        participant.setStatus(false);
    }

}
