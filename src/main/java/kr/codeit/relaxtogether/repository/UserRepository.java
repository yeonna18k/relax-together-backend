package kr.codeit.relaxtogether.repository;

import kr.codeit.relaxtogether.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
