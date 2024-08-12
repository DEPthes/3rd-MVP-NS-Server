package depth.mvp.ns.domain.theme_like.domain.repository;

import depth.mvp.ns.domain.common.Status;
import depth.mvp.ns.domain.theme.domain.Theme;
import depth.mvp.ns.domain.theme_like.domain.ThemeLike;
import depth.mvp.ns.domain.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ThemeLikeRepository extends JpaRepository<ThemeLike, Long> {
    Optional<ThemeLike> findByUserAndTheme(User user, Theme theme);
    boolean existsByThemeAndUserAndStatus(Theme theme, User user, Status status);

    List<ThemeLike> findAllByUserAndStatus(User user, Status status);

    int countByThemeAndStatus(Theme theme, Status status);

    Page<ThemeLike> findByUserAndStatus(User user, Status status, Pageable pageable);

    @Query("SELECT tl FROM ThemeLike tl WHERE tl.user = :user AND tl.status = :status AND " +
            "LOWER(tl.theme.content) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<ThemeLike> findByUserAndStatusAndThemeFieldsContaining(
            @Param("user") User user,
            @Param("status") Status status,
            @Param("keyword") String keyword
    );

    @Query("SELECT tl FROM ThemeLike tl WHERE tl.user = :user AND tl.status = :status AND " +
            "LOWER(tl.theme.content) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<ThemeLike> findPagesByUserAndStatusAndThemeFieldsContaining(
            @Param("user") User user,
            @Param("status") Status status,
            @Param("keyword") String keyword,
            Pageable pageable
    );
}
