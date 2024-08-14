package depth.mvp.ns.domain.user_point.domain.repository;

import depth.mvp.ns.domain.user_point.domain.UserPoint;
import depth.mvp.ns.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface UserPointRepository extends JpaRepository<UserPoint, Long> {

    @Query("SELECT p FROM UserPoint p WHERE p.user = :user AND DATE(p.createdDate) = :date AND p.score = :score")
    Optional<UserPoint> findByUserAndCreatedDateAndScore(
            @Param("user") User user,
            @Param("date") LocalDate date,
            @Param("score") int score
    );

}
