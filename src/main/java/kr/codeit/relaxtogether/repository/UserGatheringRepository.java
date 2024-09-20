package kr.codeit.relaxtogether.repository;

import kr.codeit.relaxtogether.entity.gathering.UserGathering;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserGatheringRepository extends JpaRepository<UserGathering, Long> {

    @Query("SELECT COUNT(ug) FROM UserGathering ug WHERE ug.gathering.id = :gatheringId")
    Long countByGatheringId(@Param("gatheringId") Long gatheringId);
}
