package kr.codeit.relaxtogether.repository;

import java.util.Optional;
import kr.codeit.relaxtogether.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);
}
