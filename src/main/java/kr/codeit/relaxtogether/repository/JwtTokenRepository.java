package kr.codeit.relaxtogether.repository;

import kr.codeit.relaxtogether.entity.JwtToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public interface JwtTokenRepository extends JpaRepository<JwtToken, Long> {

    boolean existsByToken(String token);

    @Transactional
    void deleteByToken(String token);
}
