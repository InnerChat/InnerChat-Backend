package innerchat.domain.channel.service;

import innerchat.domain.channel.dto.ChannelMemberResponse;
import innerchat.domain.channel.dto.CreateChannelRequest;
import innerchat.domain.channel.dto.CreateChannelResponse;
import innerchat.domain.channel.dto.ReadChannelListResponse;
import innerchat.domain.channel.entity.Channel;
import innerchat.domain.channel.entity.ChannelMember;
import innerchat.domain.channel.repository.ChannelMemberRepository;
import innerchat.domain.channel.repository.ChannelRepository;
import innerchat.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ChannelService {

    private final ChannelRepository channelRepository;
    private final ChannelMemberRepository channelMemberRepository;
    private final UserRepository userRepository;



    /**
     * 채널 전체 목록 가져오기
     * @return
     */
    public List<ReadChannelListResponse> getChannelList(Long userId) {
        return channelRepository.findAll().stream()
                .map(channel -> new ReadChannelListResponse(
                        channel.getChannelId(),
                        channel.getName(),
                        channel.getDescription(),
                        channel.getType(),
                        channel.getOwnerId(),
                        channelMemberRepository.countByChannelId(channel.getChannelId()),
                        channelMemberRepository.existsByChannelIdAndUserId(channel.getChannelId(), userId)
                ))
                .toList();
    }

    /**
     * 채널 생성
     * @param userId
     * @param request
     * @return
     */
    public CreateChannelResponse createChannel(Long userId, CreateChannelRequest request) {
        if (request.getChannelName() == null  || request.getChannelName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "채널 이름이 필요합니다.");
        }
        if (channelRepository.existsByName(request.getChannelName())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 존재하는 채널 이름입니다.");
        }
        Channel saved = channelRepository.save(new Channel(request.getChannelName(), request.getDescription(), userId, request.getType()));

        //생성자는 맴버로 자동 등록
        channelMemberRepository.save(new ChannelMember(saved.getChannelId(), userId));

        return new CreateChannelResponse(saved.getChannelId());
    }

    /**
     * 채널 삭제 프론트 미구현
     * @param userId
     * @param channelId
     */
    public void deleteChannel(Long userId, Long channelId) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "채널이 존재하지 않습니다."));

        if (!channel.getOwnerId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "채널 삭제 권한이 없습니다.");
        }

        channelMemberRepository.deleteAllByChannelId(channelId);
        channelRepository.delete(channel);
    }

    /**
     * 채널 참가 프론트 미구현
     * @param userId
     * @param channelId
     */
    public void joinChannel(Long userId, Long channelId) {
        if (!channelRepository.existsById(channelId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "채널이 존재하지 않습니다.");
        }
        if (channelMemberRepository.existsByChannelIdAndUserId(channelId, userId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 참가한 채널 입니다.");
        }
        channelMemberRepository.save(new ChannelMember(channelId, userId));
    }

    public List<ChannelMemberResponse> getChannelMembers(Long channelId) {
        List<Long> userIds = channelMemberRepository.findAllByChannelId(channelId)
                .stream()
                .map(ChannelMember::getUserId)
                .toList();
        return userRepository.findAllById(userIds)
                .stream()
                .map(user -> new ChannelMemberResponse(user.getUserId(), user.getUserName()))
                .toList();
    }

    public void inviteChannel(Long inviterId, Long channelId, Long targetUserId) {
        if (!channelRepository.existsById(channelId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "채널이 존재하지 않습니다.");
        }
        if (!channelMemberRepository.existsByChannelIdAndUserId(channelId, inviterId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "채널 맴버만 초대할 수 있습니다.");
        }
        if (channelMemberRepository.existsByChannelIdAndUserId(channelId, targetUserId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 채널 맴버 입니다.");
        }
        channelMemberRepository.save(new ChannelMember(channelId, targetUserId));
    }

}
