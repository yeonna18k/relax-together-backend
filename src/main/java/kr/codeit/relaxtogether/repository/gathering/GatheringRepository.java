package kr.codeit.relaxtogether.repository.gathering;

import java.util.Optional;
import kr.codeit.relaxtogether.entity.gathering.Gathering;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GatheringRepository extends JpaRepository<Gathering, Long>, GatheringRepositoryCustom {

    Optional<Gathering> findByIdAndHostUserId(Long gatheringId, Long hostUserId);
}
