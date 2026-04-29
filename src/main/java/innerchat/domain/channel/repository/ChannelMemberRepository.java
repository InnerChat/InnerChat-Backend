package innerchat.domain.channel.repository;

import innerchat.domain.channel.entity.ChannelMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChannelMemberRepository extends JpaRepository<ChannelMember, Long> {
    void deleteAllByChannelId(Long channelId);
    long countByChannelId(Long channelId);
    List<ChannelMember> findAllByChannelId(Long channelId);
    boolean existsByChannelIdAndUserId(Long channelId, Long userId);
    void deleteByChannelIdAndUserId(Long channelId, Long userId);
    List<ChannelMember> findAllByUserId(Long userId);
}
