package depth.mvp.ns.domain.theme.domain.repository;

import depth.mvp.ns.domain.theme.domain.Theme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface ThemeRepository extends JpaRepository<Theme, Long> {
    Optional<Theme> findByDate(LocalDate today);

    @Query("SELECT COUNT(l) FROM ThemeLike l WHERE l.theme.id = :themeId AND l.status = 'ACTIVE'")
    int countLikesByThemeId(@Param("themeId") Long themeId);
}