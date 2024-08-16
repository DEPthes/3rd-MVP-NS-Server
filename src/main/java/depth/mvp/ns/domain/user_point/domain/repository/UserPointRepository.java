package depth.mvp.ns.domain.user_point.domain.repository;

import depth.mvp.ns.domain.user_point.domain.UserPoint;
import depth.mvp.ns.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserPointRepository extends JpaRepository<UserPoint, Long> {

    @Query("SELECT p FROM UserPoint p WHERE p.user = :user AND p.modifiedDate >= :startOfDay AND p.modifiedDate < :endOfDay AND p.score = :score ORDER BY p.modifiedDate ASC")
    List<UserPoint> findTop1ByUserAndModifiedDateAndScore(
            @Param("user") User user,
            @Param("startOfDay") LocalDateTime startOfDay,
            @Param("endOfDay") LocalDateTime endOfDay,
            @Param("score") int score
    );





}
