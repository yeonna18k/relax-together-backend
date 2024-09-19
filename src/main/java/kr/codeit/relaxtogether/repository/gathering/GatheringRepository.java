package kr.codeit.relaxtogether.repository.gathering;

import kr.codeit.relaxtogether.entity.gathering.Gathering;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GatheringRepository extends JpaRepository<Gathering, Long>, GatheringRepositoryCustom {

}
