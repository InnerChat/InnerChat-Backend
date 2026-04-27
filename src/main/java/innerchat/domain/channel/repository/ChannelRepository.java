package innerchat.domain.channel.repository;

import innerchat.domain.channel.entity.Channel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface ChannelRepository extends JpaRepository<Channel, Long> {
    boolean existsByName(String name);
}
