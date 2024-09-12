package kr.codeit.relaxtogether.repository;

import kr.codeit.relaxtogether.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {
}
