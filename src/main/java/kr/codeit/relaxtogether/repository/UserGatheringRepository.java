package kr.codeit.relaxtogether.repository;

import kr.codeit.relaxtogether.entity.gathering.UserGathering;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserGatheringRepository extends JpaRepository<UserGathering, Long> {
}
