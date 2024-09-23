package kr.codeit.relaxtogether.repository;

import kr.codeit.relaxtogether.entity.gathering.UserGathering;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserGatheringRepository extends JpaRepository<UserGathering, Long> {

    @Query("SELECT COUNT(ug) FROM UserGathering ug WHERE ug.gathering.id = :gatheringId")
    Long countByGatheringId(@Param("gatheringId") Long gatheringId);

    boolean existsByUserIdAndGatheringId(@Param("userId") Long userId, @Param("gatheringId") Long gatheringId);

    void deleteByUserIdAndGatheringId(Long id, Long gatheringId);

    @Query("SELECT ug FROM UserGathering ug JOIN FETCH ug.user WHERE ug.gathering.id = :gatheringId")
    Page<UserGathering> findWithUserByGatheringId(@Param("gatheringId") Long gatheringId, Pageable pageable);

    @Query("SELECT ug FROM UserGathering ug JOIN FETCH ug.gathering g WHERE ug.user.id = :userId ORDER BY g.registrationEnd DESC")
    Slice<UserGathering> findGatheringsByUserId(@Param("userId") Long userId, Pageable pageable);
}
